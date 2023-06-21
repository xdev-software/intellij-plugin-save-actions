package software.xdev.saveactions.core.service;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.model.Action;
import com.intellij.openapi.actionSystem.ex.QuickList;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import java.util.List;
import java.util.Set;

/**
 * This interface is implemented by all SaveAction ApplicationServices. It is used to be able to override
 * a concrete implementation. Also, it has to be annotated with {@link Service}.
 */
@Service
public interface SaveActionsService {

    void guardedProcessPsiFiles(Project project, Set<PsiFile> psiFiles, Action activation, ExecutionMode mode);

    boolean isJavaAvailable();

    boolean isCompilingAvailable();

    List<QuickList> getQuickLists(Project project);

}
