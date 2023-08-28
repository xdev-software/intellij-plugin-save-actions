package software.xdev.saveactions.integration;

import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import software.xdev.saveactions.core.action.BatchActionConstants;
import software.xdev.saveactions.core.action.ShortcutActionConstants;
import software.xdev.saveactions.core.component.SaveActionManagerConstants;
import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import software.xdev.saveactions.junit.JUnit5Utils;
import software.xdev.saveactions.model.Storage;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.intellij.testFramework.LightProjectDescriptor.EMPTY_PROJECT_DESCRIPTOR;

public abstract class IntegrationTest {

    private CodeInsightTestFixture fixture;

    Storage storage;

    @BeforeEach
    public void before(TestInfo testInfo) throws Exception {
        IdeaTestFixtureFactory factory = IdeaTestFixtureFactory.getFixtureFactory();
        IdeaProjectTestFixture testFixture = factory.createLightFixtureBuilder(EMPTY_PROJECT_DESCRIPTOR, testInfo.getDisplayName()).getFixture();
        fixture = factory.createCodeInsightFixture(testFixture, new LightTempDirTestFixtureImpl(true));
        fixture.setUp();
        fixture.setTestDataPath(getTestDataPath());
        storage = testFixture.getProject().getService(Storage.class);
    }

    @AfterEach
    public void after() throws Exception {
        fixture.tearDown();
        storage.clear();
    }

    void assertSaveAction(ActionTestFile before, ActionTestFile after) {
        fixture.configureByFile(before.getFilename());
        SaveActionManagerConstants.SAVE_ACTION_MANAGER.accept(fixture, SaveActionsServiceManager.getService());
        JUnit5Utils.rethrowAsJunit5Error(() -> fixture.checkResultByFile(after.getFilename()));
    }

    void assertSaveActionShortcut(ActionTestFile before, ActionTestFile after) {
        fixture.configureByFile(before.getFilename());
        ShortcutActionConstants.SAVE_ACTION_SHORTCUT_MANAGER.accept(fixture);
        JUnit5Utils.rethrowAsJunit5Error(() -> fixture.checkResultByFile(after.getFilename()));
    }

    void assertSaveActionBatch(ActionTestFile before, ActionTestFile after) {
        fixture.configureByFile(before.getFilename());
        BatchActionConstants.SAVE_ACTION_BATCH_MANAGER.accept(fixture);
        JUnit5Utils.rethrowAsJunit5Error(() -> fixture.checkResultByFile(after.getFilename()));
    }

    private String getTestDataPath() {
        /* See gradle config. Previous implementation not compatible with intellij gradle plugin >= 1.6.0 */
        Path resources = Paths.get("./build/classes/java/resources");
        Path root = Paths.get(resources.toString(), getClass().getPackage().getName().split("[.]"));
        return root.toString();
    }

}
