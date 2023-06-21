package software.xdev.saveactions.core.action;

import software.xdev.saveactions.core.service.SaveActionsService;
import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import com.intellij.analysis.AnalysisScope;
import com.intellij.analysis.BaseAnalysisAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import software.xdev.saveactions.model.Action;

import java.util.HashSet;
import java.util.Set;

import static software.xdev.saveactions.core.ExecutionMode.batch;
import static software.xdev.saveactions.model.Action.activateOnBatch;
import static java.util.Collections.synchronizedSet;

/**
 * This action runs the save actions on the given scope of files, only if property
 * {@link Action#activateOnShortcut} is enabled. The user is asked for the scope using a standard
 * IDEA dialog. It delegates to {@link SaveActionsService}. Originally based on
 * {@link com.intellij.codeInspection.inferNullity.InferNullityAnnotationsAction}.
 *
 * @author markiewb
 * @see SaveActionsServiceManager
 */
public class BatchAction extends BaseAnalysisAction {

    private static final Logger LOGGER = Logger.getInstance(SaveActionsService.class);
    private static final String COMPONENT_NAME = "Save Actions";

    public BatchAction() {
        super(COMPONENT_NAME, COMPONENT_NAME);
    }

    @Override
    protected void analyze(@NotNull Project project, @NotNull AnalysisScope scope) {
        LOGGER.info("[+] Start BatchAction#analyze with project " + project + " and scope " + scope);
        Set<PsiFile> psiFiles = synchronizedSet(new HashSet<>());
        scope.accept(new PsiElementVisitor() {
            @Override
            public void visitFile(PsiFile psiFile) {
                super.visitFile(psiFile);
                psiFiles.add(psiFile);
            }
        });
        SaveActionsServiceManager.getService().guardedProcessPsiFiles(project, psiFiles, activateOnBatch, batch);
        LOGGER.info("End BatchAction#analyze processed " + psiFiles.size() + " files");
    }

}
