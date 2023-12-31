package software.xdev.saveactions.core.component;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.PsiErrorElementUtil;
import org.jetbrains.annotations.NotNull;
import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.core.service.SaveActionsService;
import software.xdev.saveactions.model.Action;
import software.xdev.saveactions.model.Storage;
import software.xdev.saveactions.processors.Processor;
import software.xdev.saveactions.processors.Result;
import software.xdev.saveactions.processors.ResultCode;
import software.xdev.saveactions.processors.SaveCommand;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.stream.Collectors.toSet;

/**
 * Implementation of the save action engine. This class will filter, process and log modifications to the files.
 */
public class Engine {

    private static final Logger LOGGER = Logger.getInstance(SaveActionsService.class);
    private static final String REGEX_STARTS_WITH_ANY_STRING = ".*?";

    private final Storage storage;
    private final List<Processor> processors;
    private final Project project;
    private final Set<PsiFile> psiFiles;
    private final Action activation;
    private final ExecutionMode mode;

    public Engine(Storage storage,
                  List<Processor> processors,
                  Project project,
                  Set<PsiFile> psiFiles,
                  Action activation,
                  ExecutionMode mode) {
        this.storage = storage;
        this.processors = processors;
        this.project = project;
        this.psiFiles = psiFiles;
        this.activation = activation;
        this.mode = mode;
    }

    public void processPsiFilesIfNecessary() {
        if (psiFiles == null) {
            return;
        }
        if (!storage.isEnabled(activation)) {
            LOGGER.info(String.format("Action \"%s\" not enabled on %s", activation.getText(), project));
            return;
        }
        LOGGER.info(String.format("Processing %s files %s mode %s", project, psiFiles, mode));
        Set<PsiFile> psiFilesEligible = psiFiles.stream()
                .filter(psiFile -> isPsiFileEligible(project, psiFile))
                .collect(toSet());
        LOGGER.info(String.format("Valid files %s", psiFilesEligible));
        processPsiFiles(project, psiFilesEligible, mode);
    }

    private void processPsiFiles(Project project, Set<PsiFile> psiFiles, ExecutionMode mode) {
        if (psiFiles.isEmpty()) {
            return;
        }
        LOGGER.info(String.format("Start processors (%d)", processors.size()));
        List<SaveCommand> processorsEligible = processors.stream()
                .map(processor -> processor.getSaveCommand(project, psiFiles))
                .filter(command -> storage.isEnabled(command.getAction()))
                .filter(command -> command.getModes().contains(mode))
                .toList();
        LOGGER.info(String.format("Filtered processors %s", processorsEligible));
        if (!processorsEligible.isEmpty()) {
            PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
            psiFiles.forEach(psiFile -> commitDocumentAndSave(psiFile, psiDocumentManager));
        }
        List<SimpleEntry<Action, Result<ResultCode>>> results = processorsEligible.stream()
                .filter(Objects::nonNull)
                .peek(command -> LOGGER.info(String.format("Execute command %s on %d files", command, psiFiles.size())))
                .map(command -> new SimpleEntry<>(command.getAction(), command.execute()))
                .toList();
        LOGGER.info(String.format("Exit engine with results %s", results.stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .toList()));
    }

    private boolean isPsiFileEligible(Project project, PsiFile psiFile) {
        return psiFile != null
                && isProjectValid(project)
                && isPsiFileValid(psiFile)
                && isPsiFileFresh(psiFile)
                && isPsiFileInProject(project, psiFile)
                && isPsiFileNoError(project, psiFile)
                && isPsiFileIncluded(psiFile);
    }

    private boolean isProjectValid(Project project) {
        boolean valid = project.isInitialized() && !project.isDisposed();
        if (!valid) {
            LOGGER.info("Project invalid. Either not initialized or disposed.");
        }
        return valid;
    }

    private boolean isPsiFileInProject(Project project, @NotNull PsiFile psiFile) {
        boolean inProject = ProjectRootManager.getInstance(project).getFileIndex().isInContent(psiFile.getVirtualFile());
        if (!inProject) {
            LOGGER.info(String.format("File %s not in current project %s. File belongs to %s",
                    psiFile,
                    project,
                    psiFile.getProject()));
        }
        return inProject;
    }

    private boolean isPsiFileNoError(Project project, PsiFile psiFile) {
        if (storage.isEnabled(Action.noActionIfCompileErrors)) {
            boolean hasErrors = PsiErrorElementUtil.hasErrors(project, psiFile.getVirtualFile());
            if (hasErrors) {
                LOGGER.info(String.format("File %s has errors", psiFile));
            }
            return !hasErrors;
        }
        return true;
    }

    private boolean isPsiFileIncluded(PsiFile psiFile) {
        String canonicalPath = psiFile.getVirtualFile().getCanonicalPath();
        return isIncludedAndNotExcluded(canonicalPath);
    }

    private boolean isPsiFileFresh(PsiFile psiFile) {
        if (mode == ExecutionMode.batch) {
            return true;
        }
        boolean isFresh = psiFile.getModificationStamp() != 0;
        if (!isFresh) {
            LOGGER.info(String.format("File %s is not fresh.", psiFile));
        }
        return isFresh;
    }

    private boolean isPsiFileValid(PsiFile psiFile) {
        boolean valid = psiFile.isValid();
        if (!valid) {
            LOGGER.info(String.format("File %s is not valid.", psiFile));
        }
        return valid;
    }

    boolean isIncludedAndNotExcluded(String path) {
        return isIncluded(path) && !isExcluded(path);
    }

    private boolean isExcluded(String path) {
        Set<String> exclusions = storage.getExclusions();
        boolean psiFileExcluded = atLeastOneMatch(path, exclusions);
        if (psiFileExcluded) {
            LOGGER.info(String.format("File %s excluded in %s", path, exclusions));
        }
        return psiFileExcluded;
    }

    private boolean isIncluded(String path) {
        Set<String> inclusions = storage.getInclusions();
        if (inclusions.isEmpty()) {
            // If no inclusion are defined, all files are allowed
            return true;
        }
        boolean psiFileIncluded = atLeastOneMatch(path, inclusions);
        if (psiFileIncluded) {
            LOGGER.info(String.format("File %s included in %s", path, inclusions));
        }
        return psiFileIncluded;
    }

    private boolean atLeastOneMatch(String psiFileUrl, Set<String> patterns) {
        for (String pattern : patterns) {
            try {
                Matcher matcher = Pattern.compile(REGEX_STARTS_WITH_ANY_STRING + pattern).matcher(psiFileUrl);
                if (matcher.matches()) {
                    return true;
                }
            } catch (PatternSyntaxException e) {
                // invalid patterns are ignored
                return false;
            }
        }
        return false;
    }

    /**
     * Should properly fix #402 according to @krasa's recommendation in #109.
     *
     * @param psiFile            of type PsiFile
     * @param psiDocumentManager
     */
    private void commitDocumentAndSave(PsiFile psiFile, PsiDocumentManager psiDocumentManager) {
        Document document = psiDocumentManager.getDocument(psiFile);
        if (document != null) {
            psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
            psiDocumentManager.commitDocument(document);
            FileDocumentManager.getInstance().saveDocument(document);
        }
    }
}
