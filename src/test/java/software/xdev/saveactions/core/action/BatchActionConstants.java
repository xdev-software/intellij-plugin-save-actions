package software.xdev.saveactions.core.action;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import software.xdev.saveactions.core.action.BatchAction;

import java.util.function.Consumer;

public interface BatchActionConstants {

    Consumer<CodeInsightTestFixture> SAVE_ACTION_BATCH_MANAGER = (fixture) ->
            WriteCommandAction.writeCommandAction(fixture.getProject()).run(() -> runFixure(fixture));

    static void runFixure(CodeInsightTestFixture fixture) {
        // set modification timestamp ++
        fixture.getFile().clearCaches();

        // call plugin on document
        new BatchAction().analyze(fixture.getProject(), new AnalysisScope(fixture.getProject()));
    }
}
