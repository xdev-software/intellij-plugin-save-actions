package software.xdev.saveactions.core.component;

import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.ApplicationKt;
import com.intellij.util.PsiErrorElementUtil;
import com.intellij.util.ThrowableRunnable;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.core.service.SaveActionsService;
import software.xdev.saveactions.model.Action;
import software.xdev.saveactions.model.Storage;
import software.xdev.saveactions.processors.Processor;
import software.xdev.saveactions.processors.Result;
import software.xdev.saveactions.processors.ResultCode;
import software.xdev.saveactions.processors.SaveCommand;


/**
 * Implementation of the save action engine. This class will filter, process and log modifications to the files.
 */
public class Engine
{
	private static final Logger LOGGER = Logger.getInstance(SaveActionsService.class);
	private static final String REGEX_STARTS_WITH_ANY_STRING = ".*?";
	
	private final Storage storage;
	private final List<Processor> processors;
	private final Project project;
	private final Set<PsiFile> psiFiles;
	private final Action activation;
	private final ExecutionMode mode;
	
	public Engine(
		final Storage storage,
		final List<Processor> processors,
		final Project project,
		final Set<PsiFile> psiFiles,
		final Action activation,
		final ExecutionMode mode)
	{
		this.storage = storage;
		this.processors = processors;
		this.project = project;
		this.psiFiles = psiFiles;
		this.activation = activation;
		this.mode = mode;
	}
	
	public void processPsiFilesIfNecessary(
		@NotNull final ProgressIndicator indicator,
		final boolean async)
	{
		if(this.psiFiles == null)
		{
			return;
		}
		if(!this.storage.isEnabled(this.activation))
		{
			LOGGER.info(String.format("Action \"%s\" not enabled on %s", this.activation.getText(), this.project));
			return;
		}
		
		indicator.setIndeterminate(true);
		final Set<PsiFile> psiFilesEligible = this.getEligiblePsiFiles(indicator, async);
		if(psiFilesEligible.isEmpty())
		{
			LOGGER.info("No files are eligible");
			return;
		}
		
		final List<SaveCommand> processorsEligible = this.getEligibleProcessors(indicator, psiFilesEligible);
		if(processorsEligible.isEmpty())
		{
			LOGGER.info("No processors are eligible");
			return;
		}
		
		this.flushPsiFiles(indicator, async, psiFilesEligible);
		
		this.execute(indicator, processorsEligible, psiFilesEligible);
	}
	
	private Set<PsiFile> getEligiblePsiFiles(final @NotNull ProgressIndicator indicator, final boolean async)
	{
		LOGGER.info(String.format("Processing %s files %s mode %s", this.project, this.psiFiles, this.mode));
		indicator.checkCanceled();
		indicator.setText2("Collecting files to process");
		
		final ThrowableComputable<Set<PsiFile>, RuntimeException> psiFilesEligibleFunc =
			() -> this.psiFiles.stream()
				.filter(psiFile -> this.isPsiFileEligible(this.project, psiFile))
				.collect(toSet());
		final Set<PsiFile> psiFilesEligible = async
			? ReadAction.compute(psiFilesEligibleFunc)
			: psiFilesEligibleFunc.compute();
		LOGGER.info(String.format("Valid files %s", psiFilesEligible));
		return psiFilesEligible;
	}
	
	private @NotNull List<SaveCommand> getEligibleProcessors(
		final @NotNull ProgressIndicator indicator,
		final Set<PsiFile> psiFilesEligible)
	{
		LOGGER.info(String.format("Start processors (%d)", this.processors.size()));
		indicator.checkCanceled();
		indicator.setText2("Collecting processors");
		
		final List<SaveCommand> processorsEligible = this.processors.stream()
			.map(processor -> processor.getSaveCommand(this.project, psiFilesEligible))
			.filter(command -> this.storage.isEnabled(command.getAction()))
			.filter(command -> command.getModes().contains(this.mode))
			.toList();
		LOGGER.info(String.format("Filtered processors %s", processorsEligible));
		return processorsEligible;
	}
	
	private void flushPsiFiles(
		final @NotNull ProgressIndicator indicator,
		final boolean async,
		final Set<PsiFile> psiFilesEligible)
	{
		LOGGER.info(String.format("Flushing files (%d)", psiFilesEligible.size()));
		indicator.checkCanceled();
		indicator.setText2("Flushing files");
		
		final ThrowableRunnable<RuntimeException> flushFilesFunc = () -> {
			final PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(this.project);
			psiFilesEligible.forEach(psiFile -> this.commitDocumentAndSave(psiFile, psiDocumentManager));
		};
		if(async)
		{
			ApplicationKt.getApplication().invokeAndWait(() -> WriteAction.run(flushFilesFunc));
		}
		else
		{
			flushFilesFunc.run();
		}
	}
	
	private void execute(
		final @NotNull ProgressIndicator indicator,
		final List<SaveCommand> processorsEligible,
		final Set<PsiFile> psiFilesEligible)
	{
		indicator.checkCanceled();
		indicator.setIndeterminate(false);
		indicator.setFraction(0d);
		
		final List<SaveCommand> saveCommands = processorsEligible.stream()
			.filter(Objects::nonNull)
			.toList();
		
		final AtomicInteger executedCount = new AtomicInteger();
		final List<SimpleEntry<Action, Result<ResultCode>>> results = saveCommands.stream()
			.map(command -> {
				LOGGER.info(String.format("Execute command %s on %d files", command, psiFilesEligible.size()));
				
				indicator.checkCanceled();
				indicator.setText2("Executing '" + command.getAction().getText() + "'");
				
				final SimpleEntry<Action, Result<ResultCode>> entry =
					new SimpleEntry<>(command.getAction(), command.execute());
				
				indicator.setFraction((double)executedCount.incrementAndGet() / saveCommands.size());
				
				return entry;
			})
			.toList();
		LOGGER.info(String.format("Exit engine with results %s", results.stream()
			.map(entry -> entry.getKey() + ":" + entry.getValue())
			.toList()));
	}
	
	private boolean isPsiFileEligible(final Project project, final PsiFile psiFile)
	{
		return psiFile != null
			&& this.isProjectValid(project)
			&& this.isPsiFileValid(psiFile)
			&& this.hasPsiFileText(psiFile)
			&& this.isPsiFileFresh(psiFile)
			&& this.isPsiFileInProject(project, psiFile)
			&& this.isPsiFileNoError(project, psiFile)
			&& this.isPsiFileIncluded(psiFile);
	}
	
	private boolean isProjectValid(final Project project)
	{
		final boolean valid = project.isInitialized() && !project.isDisposed();
		if(!valid)
		{
			LOGGER.info("Project invalid. Either not initialized or disposed.");
		}
		return valid;
	}
	
	private boolean isPsiFileInProject(final Project project, @NotNull final PsiFile psiFile)
	{
		final boolean inProject =
			ProjectRootManager.getInstance(project).getFileIndex().isInContent(psiFile.getVirtualFile());
		if(!inProject)
		{
			LOGGER.info(String.format(
				"File %s not in current project %s. File belongs to %s",
				psiFile,
				project,
				psiFile.getProject()));
		}
		return inProject;
	}
	
	private boolean isPsiFileNoError(final Project project, final PsiFile psiFile)
	{
		if(this.storage.isEnabled(Action.noActionIfCompileErrors))
		{
			final boolean hasErrors = PsiErrorElementUtil.hasErrors(project, psiFile.getVirtualFile());
			if(hasErrors)
			{
				LOGGER.info(String.format("File %s has errors", psiFile));
			}
			return !hasErrors;
		}
		return true;
	}
	
	private boolean isPsiFileIncluded(final PsiFile psiFile)
	{
		final String canonicalPath = psiFile.getVirtualFile().getCanonicalPath();
		return this.isIncludedAndNotExcluded(canonicalPath);
	}
	
	private boolean isPsiFileFresh(final PsiFile psiFile)
	{
		if(this.mode == ExecutionMode.batch)
		{
			return true;
		}
		final boolean isFresh = psiFile.getModificationStamp() != 0;
		if(!isFresh)
		{
			LOGGER.info(String.format("File %s is not fresh.", psiFile));
		}
		return isFresh;
	}
	
	private boolean isPsiFileValid(final PsiFile psiFile)
	{
		final boolean valid = psiFile.isValid();
		if(!valid)
		{
			LOGGER.info(String.format("File %s is not valid.", psiFile));
		}
		return valid;
	}
	
	private boolean hasPsiFileText(final PsiFile psiFile)
	{
		final boolean valid = psiFile.getTextRange() != null;
		if(!valid)
		{
			LOGGER.info(String.format("File %s has no text.", psiFile));
		}
		return valid;
	}
	
	boolean isIncludedAndNotExcluded(final String path)
	{
		return this.isIncluded(path) && !this.isExcluded(path);
	}
	
	private boolean isExcluded(final String path)
	{
		final Set<String> exclusions = this.storage.getExclusions();
		final boolean psiFileExcluded = this.atLeastOneMatch(path, exclusions);
		if(psiFileExcluded)
		{
			LOGGER.info(String.format("File %s excluded in %s", path, exclusions));
		}
		return psiFileExcluded;
	}
	
	private boolean isIncluded(final String path)
	{
		final Set<String> inclusions = this.storage.getInclusions();
		if(inclusions.isEmpty())
		{
			// If no inclusion are defined, all files are allowed
			return true;
		}
		final boolean psiFileIncluded = this.atLeastOneMatch(path, inclusions);
		if(psiFileIncluded)
		{
			LOGGER.info(String.format("File %s included in %s", path, inclusions));
		}
		return psiFileIncluded;
	}
	
	private boolean atLeastOneMatch(final String psiFileUrl, final Set<String> patterns)
	{
		for(final String pattern : patterns)
		{
			try
			{
				final Matcher matcher = Pattern.compile(REGEX_STARTS_WITH_ANY_STRING + pattern).matcher(psiFileUrl);
				if(matcher.matches())
				{
					return true;
				}
			}
			catch(final PatternSyntaxException e)
			{
				// invalid patterns are ignored
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Should properly fix #402 according to @krasa's recommendation in #109.
	 *
	 * @param psiFile of type PsiFile
	 */
	private void commitDocumentAndSave(final PsiFile psiFile, final PsiDocumentManager psiDocumentManager)
	{
		final Document document = psiDocumentManager.getDocument(psiFile);
		if(document != null)
		{
			psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
			psiDocumentManager.commitDocument(document);
			FileDocumentManager.getInstance().saveDocument(document);
		}
	}
}
