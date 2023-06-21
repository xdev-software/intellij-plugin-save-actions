package software.xdev.saveactions.core.component;

import software.xdev.saveactions.core.service.SaveActionsService;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static software.xdev.saveactions.core.ExecutionMode.saveAll;
import static software.xdev.saveactions.model.Action.activate;
import static java.util.Collections.singleton;

public interface SaveActionManagerConstants {

    BiConsumer<CodeInsightTestFixture, SaveActionsService> SAVE_ACTION_MANAGER = (fixture, saveActionService) ->
            WriteCommandAction.writeCommandAction(fixture.getProject()).run(() -> runFixture(fixture, saveActionService));

    static void runFixture(CodeInsightTestFixture fixture, SaveActionsService saveActionService) {
        // set modification timestamp ++
        fixture.getFile().clearCaches();

        // call plugin on document
        Set<PsiFile> psiFiles = new HashSet<>(singleton(fixture.getFile()));
        saveActionService.guardedProcessPsiFiles(fixture.getProject(), psiFiles, activate, saveAll);
    }
}
