package software.xdev.saveactions.core.listener;

import software.xdev.saveactions.core.service.SaveActionsService;
import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.model.Action;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FileDocumentManagerListener to catch save events. This listener is registered as ExtensionPoint.
 */
public final class SaveActionsDocumentManagerListener implements FileDocumentManagerListener {

    private static final Logger LOGGER = Logger.getInstance(SaveActionsService.class);

    private final Project project;
    private PsiDocumentManager psiDocumentManager = null;

    public SaveActionsDocumentManagerListener(Project project) {
        this.project = project;
    }

    @Override
    public void beforeAllDocumentsSaving() {
        LOGGER.debug("[+] Start SaveActionsDocumentManagerListener#beforeAllDocumentsSaving, " + project.getName());
        List<Document> unsavedDocuments = Arrays.asList(FileDocumentManager.getInstance().getUnsavedDocuments());
        if (!unsavedDocuments.isEmpty()) {
            LOGGER.debug(String.format("Locating psi files for %d documents: %s", unsavedDocuments.size(), unsavedDocuments));
            beforeDocumentsSaving(unsavedDocuments);
        }
        LOGGER.debug("End SaveActionsDocumentManagerListener#beforeAllDocumentsSaving");
    }

    public void beforeDocumentsSaving(List<Document> documents) {
        if (project.isDisposed()) {
            return;
        }
        initPsiDocManager();
        Set<PsiFile> psiFiles = documents.stream()
                .map(psiDocumentManager::getPsiFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        SaveActionsServiceManager.getService().guardedProcessPsiFiles(project, psiFiles, Action.activate, ExecutionMode.saveAll);
    }

    private synchronized void initPsiDocManager() {
        if (psiDocumentManager == null) {
            psiDocumentManager = PsiDocumentManager.getInstance(project);
        }
    }

}
