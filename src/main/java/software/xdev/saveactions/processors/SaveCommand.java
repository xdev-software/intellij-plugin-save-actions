package software.xdev.saveactions.processors;

import java.util.Set;
import java.util.function.BiFunction;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.model.Action;


/**
 * Abstracts a save command with a {@link BiFunction} from pair ({@link Project}, {@link PsiFile}[]) to
 * {@link Runnable}. The entry point is {@link #execute()}.
 */
public abstract class SaveCommand
{
	private final Project project;
	private final Set<PsiFile> psiFiles;
	private final Set<ExecutionMode> modes;
	private final Action action;
	private final BiFunction<Project, PsiFile[], Runnable> command;
	
	protected SaveCommand(
		final Project project, final Set<PsiFile> psiFiles, final Set<ExecutionMode> modes, final Action action,
		final BiFunction<Project, PsiFile[], Runnable> command)
	{
		this.project = project;
		this.psiFiles = psiFiles;
		this.modes = modes;
		this.action = action;
		this.command = command;
	}
	
	public Project getProject()
	{
		return this.project;
	}
	
	public Set<PsiFile> getPsiFiles()
	{
		return this.psiFiles;
	}
	
	public PsiFile[] getPsiFilesAsArray()
	{
		return this.psiFiles.toArray(new PsiFile[0]);
	}
	
	public Set<ExecutionMode> getModes()
	{
		return this.modes;
	}
	
	public Action getAction()
	{
		return this.action;
	}
	
	public BiFunction<Project, PsiFile[], Runnable> getCommand()
	{
		return this.command;
	}
	
	@Override
	public String toString()
	{
		return this.action.toString();
	}
	
	public abstract Result<ResultCode> execute();
}
