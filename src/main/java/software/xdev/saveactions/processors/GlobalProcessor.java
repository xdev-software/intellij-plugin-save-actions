package software.xdev.saveactions.processors;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.actions.AbstractLayoutCodeProcessor;
import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.codeInsight.actions.RearrangeCodeProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.ui.EDT;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.model.Action;


/**
 * Available processors for global.
 */
@SuppressWarnings("java:S115")
public enum GlobalProcessor implements Processor
{
	organizeImports(Action.organizeImports, GlobalProcessor::optimizeImports),
	
	reformat(
		Action.reformat,
		(project, psiFiles) -> reformatCode(project, psiFiles, false)),
	
	reformatChangedCode(
		Action.reformatChangedCode,
		(project, psiFiles) -> reformatCode(project, psiFiles, true)),
	
	rearrange(Action.rearrange, GlobalProcessor::rearrangeCode);
	
	private static final Map<Action, GlobalProcessor> ACTION_VALUES = stream()
		.collect(Collectors.toMap(Processor::getAction, Function.identity()));
	
	@NotNull
	private static Runnable optimizeImports(final Project project, final Set<PsiFile> psiFiles)
	{
		return useOptimizedProcessor(
			psiFiles,
			f -> new OptimizeImportsProcessor(project, f),
			f -> new OptimizeImportsProcessor(project, f, null));
	}
	
	@NotNull
	private static Runnable reformatCode(
		final Project project,
		final Set<PsiFile> psiFiles,
		final boolean processChangedTextOnly)
	{
		return useOptimizedProcessor(
			psiFiles,
			f -> new SaveReformatCodeProcessor(project, f, processChangedTextOnly),
			f -> new SaveReformatCodeProcessor(project, f, processChangedTextOnly));
	}
	
	/**
	 * Improved version of {@link ReformatCodeProcessor}:
	 * <ul>
	 *     <li>Correctly uses Threads</li>
	 *     <li>Removes the "2nd Reformat" popup</li>
	 * </ul>
	 */
	static class SaveReformatCodeProcessor extends ReformatCodeProcessor
	{
		protected static final String SECOND_REFORMAT_CONFIRMED = "second.reformat.confirmed.2";
		
		protected final boolean requiresBGTWhenCalledFromEDT;
		
		SaveReformatCodeProcessor(
			final Project project,
			final PsiFile file,
			final boolean processChangedTextOnly)
		{
			super(project, file, null, processChangedTextOnly);
			this.requiresBGTWhenCalledFromEDT = false;
		}
		
		SaveReformatCodeProcessor(
			final Project project,
			final PsiFile[] files,
			final boolean processChangedTextOnly)
		{
			super(project, files, null, processChangedTextOnly);
			// The upstream code in AbstractLayoutCodeProcessor#run assumes that - if it is not a single file -
			// execution is already happening on BGT. This is not the case and causes
			// problems like "GitContentRevision.getContentAsBytes() should not be called from EDT" #347
			this.requiresBGTWhenCalledFromEDT = true;
		}
		
		@Override
		public void run()
		{
			if(!this.requiresBGTWhenCalledFromEDT || !EDT.isCurrentThreadEdt())
			{
				super.run();
				return;
			}
			
			ApplicationManager.getApplication().executeOnPooledThread(super::run);
		}
		
		@Override
		protected @NotNull FutureTask<Boolean> prepareTask(
			@NotNull final PsiFile psiFile,
			final boolean processChangedTextOnly)
		{
			// It would be better if we could just override confirmSecondReformat however the method is private -.-
			final CodeInsightSettings codeInsightSettings = CodeInsightSettings.getInstance();
			final boolean prevEnableSecondReformat = codeInsightSettings.ENABLE_SECOND_REFORMAT;
			codeInsightSettings.ENABLE_SECOND_REFORMAT = false;
			
			final PropertiesComponent propertiesComp = PropertiesComponent.getInstance();
			// If the value is set it can only be true
			final boolean prevSecondReformatConfirmedPresent = propertiesComp.isValueSet(SECOND_REFORMAT_CONFIRMED);
			propertiesComp.setValue(SECOND_REFORMAT_CONFIRMED, true);
			
			try
			{
				return super.prepareTask(psiFile, processChangedTextOnly);
			}
			finally
			{
				codeInsightSettings.ENABLE_SECOND_REFORMAT = prevEnableSecondReformat;
				if(!prevSecondReformatConfirmedPresent)
				{
					propertiesComp.unsetValue(SECOND_REFORMAT_CONFIRMED);
				}
			}
		}
	}
	
	@NotNull
	private static Runnable rearrangeCode(final Project project, final Set<PsiFile> psiFiles)
	{
		return useOptimizedProcessor(
			psiFiles,
			RearrangeCodeProcessor::new,
			f -> new RearrangeCodeProcessor(
				project,
				f,
				CodeInsightBundle.message("command.rearrange.code"),
				null));
	}
	
	private static <P extends AbstractLayoutCodeProcessor> Runnable useOptimizedProcessor(
		final Set<PsiFile> psiFiles,
		final Function<PsiFile, P> singleFileConstructor,
		final Function<PsiFile[], P> multiFilesConstructor)
	{
		// AbstractLayoutCodeProcessor#run differs between single files and multiple files!
		// When using single files it dispatches a BGT for the task
		// when using multiple files it does not
		return (psiFiles.size() == 1
			? singleFileConstructor.apply(psiFiles.iterator().next())
			: multiFilesConstructor.apply(psiFiles.toArray(PsiFile[]::new)))::run;
	}
	
	private final Action action;
	private final BiFunction<Project, Set<PsiFile>, Runnable> command;
	
	GlobalProcessor(final Action action, final BiFunction<Project, Set<PsiFile>, Runnable> command)
	{
		this.action = action;
		this.command = command;
	}
	
	@Override
	public Action getAction()
	{
		return this.action;
	}
	
	@Override
	public Set<ExecutionMode> getModes()
	{
		return EnumSet.allOf(ExecutionMode.class);
	}
	
	@Override
	public int getOrder()
	{
		return 3;
	}
	
	@Override
	public SaveWriteCommand createSaveCommand(final Project project, final Set<PsiFile> psiFiles)
	{
		return new SaveWriteCommand(
			project,
			psiFiles,
			this.getModes(),
			this.getAction(),
			this.getCommand());
	}
	
	public BiFunction<Project, Set<PsiFile>, Runnable> getCommand()
	{
		return this.command;
	}
	
	public static Optional<GlobalProcessor> getProcessorForAction(final Action action)
	{
		return Optional.ofNullable(ACTION_VALUES.get(action));
	}
	
	public static Stream<GlobalProcessor> stream()
	{
		return Arrays.stream(values());
	}
}
