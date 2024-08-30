package software.xdev.saveactions.processors;

import java.util.Set;
import java.util.function.BiFunction;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.model.Action;


/**
 * Implements a read action that returns a {@link Result}.
 */
public class SaveReadCommand extends SaveCommand
{
	public SaveReadCommand(
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
			this.getCommand().apply(this.getProject(), this.getPsiFilesAsArray()).run();
			return new Result<>(ResultCode.OK);
		}
		catch(final Exception e)
		{
			return new Result<>(ResultCode.FAILED);
		}
	}
}
