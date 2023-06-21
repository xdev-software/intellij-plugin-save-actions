package software.xdev.saveactions.processors;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.model.Action;
import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.codeInsight.actions.RearrangeCodeProcessor;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;


/**
 * Available processors for global.
 */
public enum GlobalProcessor implements Processor {

    organizeImports(Action.organizeImports, GlobalProcessor::optimizeImports),

    reformat(Action.reformat,
            (project, psiFiles) -> reformateCode(project, psiFiles, false)),

    reformatChangedCode(Action.reformatChangedCode,
            (project, psiFiles) -> reformateCode(project, psiFiles, true)),

    rearrange(Action.rearrange, GlobalProcessor::rearrangeCode),

    ;

    @NotNull
    private static Runnable rearrangeCode(Project project, PsiFile[] psiFiles) {
        return new RearrangeCodeProcessor(project, psiFiles, CodeInsightBundle.message("command.rearrange.code"), null)::run;
    }

    @NotNull
    private static Runnable optimizeImports(Project project, PsiFile[] psiFiles) {
        return new OptimizeImportsProcessor(project, psiFiles, null)::run;
    }

    @NotNull
    private static Runnable reformateCode(Project project, PsiFile[] psiFiles, boolean processChangedTextOnly) {
        return new ReformatCodeProcessor(project, psiFiles, null, processChangedTextOnly)::run;
    }

    private final Action action;
    private final BiFunction<Project, PsiFile[], Runnable> command;

    GlobalProcessor(Action action, BiFunction<Project, PsiFile[], Runnable> command) {
        this.action = action;
        this.command = command;
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public Set<ExecutionMode> getModes() {
        return EnumSet.allOf(ExecutionMode.class);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public SaveWriteCommand getSaveCommand(Project project, Set<PsiFile> psiFiles) {
        return new SaveWriteCommand(project, psiFiles, getModes(), getAction(), getCommand());
    }

    public BiFunction<Project, PsiFile[], Runnable> getCommand() {
        return command;
    }

    public static Optional<Processor> getProcessorForAction(Action action) {
        return stream().filter(processor -> processor.getAction().equals(action)).findFirst();
    }

    public static Stream<Processor> stream() {
        return Arrays.stream(values());
    }

}
