package software.xdev.saveactions.processors;

import java.util.Set;
import java.util.function.BiFunction;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.model.Action;


/**
 * Implements a write action that encapsulates {@link com.intellij.openapi.command.WriteCommandAction} that returns a
 * {@link Result}.
 */
public class SaveWriteCommand extends SaveCommand
{
	public SaveWriteCommand(
		final Project project, final Set<PsiFile> psiFiles, final Set<ExecutionMode> modes, final Action action,
		final BiFunction<Project, PsiFile[], Runnable> command)
	{
		super(project, psiFiles, modes, action, command);
	}
	
	@Override
	public synchronized Result<ResultCode> execute()
	{
		try
		{
			WriteCommandAction.writeCommandAction(this.getProject(), this.getPsiFilesAsArray())
				.run(() -> this.getCommand().apply(this.getProject(), this.getPsiFilesAsArray()).run());
			return new Result<>(ResultCode.OK);
		}
		catch(final Exception e)
		{
			return new Result<>(ResultCode.FAILED);
		}
	}
}
