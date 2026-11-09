package software.xdev.saveactions.core.action;

import static software.xdev.saveactions.model.Action.activate;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

import software.xdev.saveactions.model.PersistentStorageState;


/**
 * This action toggles on and off the plugin, by modifying the underlying storage.
 */
public class ToggleAnAction extends ToggleAction implements DumbAware
{
	@Override
	public boolean isSelected(final AnActionEvent event)
	{
		final Project project = event.getProject();
		if(project != null)
		{
			return project.getService(PersistentStorageState.class).getState().isEnabled(activate);
		}
		return false;
	}
	
	@Override
	public void setSelected(final AnActionEvent event, final boolean state)
	{
		final Project project = event.getProject();
		if(project != null)
		{
			project.getService(PersistentStorageState.class).getState().setEnabled(activate, state);
		}
	}
	
	@Override
	public @NotNull ActionUpdateThread getActionUpdateThread()
	{
		return ActionUpdateThread.BGT;
	}
}
