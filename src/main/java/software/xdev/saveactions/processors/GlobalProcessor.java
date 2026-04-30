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
import org.jetbrains.annotations.Nullable;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.codeInsight.actions.RearrangeCodeProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

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
	private static Runnable rearrangeCode(final Project project, final PsiFile[] psiFiles)
	{
		return new RearrangeCodeProcessor(
			project,
			psiFiles,
			CodeInsightBundle.message("command.rearrange.code"),
			null)::run;
	}
	
	@NotNull
	private static Runnable optimizeImports(final Project project, final PsiFile[] psiFiles)
	{
		return new OptimizeImportsProcessor(project, psiFiles, null)::run;
	}
	
	@NotNull
	private static Runnable reformatCode(
		final Project project,
		final PsiFile[] psiFiles,
		final boolean processChangedTextOnly)
	{
		return new IgnoreSecondReformatReformatCodeProcessor(project, psiFiles, null, processChangedTextOnly)::run;
	}
	
	static class IgnoreSecondReformatReformatCodeProcessor extends ReformatCodeProcessor
	{
		protected static final String SECOND_REFORMAT_CONFIRMED = "second.reformat.confirmed.2";
		
		IgnoreSecondReformatReformatCodeProcessor(
			final Project project,
			final PsiFile[] files,
			@Nullable final Runnable postRunnable,
			final boolean processChangedTextOnly)
		{
			super(project, files, postRunnable, processChangedTextOnly);
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
	
	private final Action action;
	private final BiFunction<Project, PsiFile[], Runnable> command;
	
	GlobalProcessor(final Action action, final BiFunction<Project, PsiFile[], Runnable> command)
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
	public SaveWriteCommand getSaveCommand(final Project project, final Set<PsiFile> psiFiles)
	{
		return new SaveWriteCommand(project, psiFiles, this.getModes(), this.getAction(), this.getCommand());
	}
	
	public BiFunction<Project, PsiFile[], Runnable> getCommand()
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
