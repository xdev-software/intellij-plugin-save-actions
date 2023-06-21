package software.xdev.saveactions.processors;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.model.Action;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import java.util.Set;
import java.util.function.BiFunction;

/**
 * Implements a write action that encapsulates {@link com.intellij.openapi.command.WriteCommandAction} that returns
 * a {@link Result}.
 */
public class SaveWriteCommand extends SaveCommand {

    public SaveWriteCommand(Project project, Set<PsiFile> psiFiles, Set<ExecutionMode> modes, Action action,
                            BiFunction<Project, PsiFile[], Runnable> command) {
        super(project, psiFiles, modes, action, command);
    }

    @Override
    public synchronized Result<ResultCode> execute() {
        try {
            WriteCommandAction.writeCommandAction(getProject(), getPsiFilesAsArray())
                    .run(() -> getCommand().apply(getProject(), getPsiFilesAsArray()).run());
            return new Result<>(ResultCode.OK);
        } catch (Exception e) {
            return new Result<>(ResultCode.FAILED);
        }
    }
}
