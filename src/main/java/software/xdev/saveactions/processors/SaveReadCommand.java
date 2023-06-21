package software.xdev.saveactions.processors;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.model.Action;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import java.util.Set;
import java.util.function.BiFunction;

/**
 * Implements a read action that returns a {@link Result}.
 */
public class SaveReadCommand extends SaveCommand {

  public SaveReadCommand(
          Project project, Set<PsiFile> psiFiles, Set<ExecutionMode> modes, Action action,
          BiFunction<Project, PsiFile[], Runnable> command) {

    super(project, psiFiles, modes, action, command);
  }

  @Override
  public synchronized Result<ResultCode> execute() {
    try {
      getCommand().apply(getProject(), getPsiFilesAsArray()).run();
      return new Result<>(ResultCode.OK);
    } catch (Exception e) {
      return new Result<>(ResultCode.FAILED);
    }
  }

}
