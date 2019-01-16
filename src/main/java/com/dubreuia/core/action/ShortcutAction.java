package com.dubreuia.core.action;

import com.dubreuia.core.component.SaveActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import static com.dubreuia.core.ExecutionMode.shortcut;
import static com.dubreuia.core.component.SaveActionManager.LOGGER;
import static com.dubreuia.model.Action.activateOnShortcut;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

/**
 * This action runs the plugin on shortcut, only if property {@link com.dubreuia.model.Action#activateOnShortcut} is
 * enabled. It delegates to {@link SaveActionManager}.
 *
 * @see SaveActionManager
 */
public class ShortcutAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        LOGGER.info("[ENTRY POINT] " + getClass().getName() + " with event " + event);
        PsiFile psiFile = event.getData(PSI_FILE);
        Project project = event.getProject();
        PsiFile[] psiFiles = new PsiFile[]{psiFile};
        // TODO array
        SaveActionManager.getInstance().processPsiFileIfNecessary(project, psiFiles, activateOnShortcut, shortcut);
    }

}
