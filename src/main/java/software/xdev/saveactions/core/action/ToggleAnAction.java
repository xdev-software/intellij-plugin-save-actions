package software.xdev.saveactions.core.action;

import software.xdev.saveactions.model.Storage;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

import static software.xdev.saveactions.model.Action.activate;

/**
 * This action toggles on and off the plugin, by modifying the underlying storage.
 */
public class ToggleAnAction extends ToggleAction implements DumbAware {

    @Override
    public boolean isSelected(AnActionEvent event) {
        Project project = event.getProject();
        if (project != null) {
            Storage storage = project.getService(Storage.class);
            return storage.isEnabled(activate);
        }
        return false;
    }

    @Override
    public void setSelected(AnActionEvent event, boolean state) {
        Project project = event.getProject();
        if (project != null) {
            Storage storage = project.getService(Storage.class);
            storage.setEnabled(activate, state);
        }
    }

}
