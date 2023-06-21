package software.xdev.saveactions.core.action;

import software.xdev.saveactions.core.service.SaveActionsService;
import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import software.xdev.saveactions.model.Action;

import java.util.HashSet;
import java.util.Set;

import static software.xdev.saveactions.core.ExecutionMode.shortcut;
import static software.xdev.saveactions.model.Action.activateOnShortcut;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;
import static java.util.Collections.singletonList;

/**
 * This action runs the plugin on shortcut, only if property {@link Action#activateOnShortcut} is
 * enabled. It delegates to {@link SaveActionsService}.
 *
 * @see SaveActionsServiceManager
 */
public class ShortcutAction extends AnAction {

    private static final Logger LOGGER = Logger.getInstance(SaveActionsService.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LOGGER.info("[+] Start ShortcutAction#actionPerformed with event " + event);
        PsiFile psiFile = event.getData(PSI_FILE);
        Project project = event.getProject();
        Set<PsiFile> psiFiles = new HashSet<>(singletonList(psiFile));
        SaveActionsServiceManager.getService().guardedProcessPsiFiles(project, psiFiles, activateOnShortcut, shortcut);
        LOGGER.info("End ShortcutAction#actionPerformed processed " + psiFiles.size() + " files");
    }

}
