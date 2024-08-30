package software.xdev.saveactions.integration;

import static com.intellij.testFramework.LightProjectDescriptor.EMPTY_PROJECT_DESCRIPTOR;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;

import software.xdev.saveactions.core.action.BatchActionConstants;
import software.xdev.saveactions.core.action.ShortcutActionConstants;
import software.xdev.saveactions.core.component.SaveActionManagerConstants;
import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import software.xdev.saveactions.junit.JUnit5Utils;
import software.xdev.saveactions.model.Storage;


public abstract class IntegrationTest
{
	private CodeInsightTestFixture fixture;
	
	Storage storage;
	
	@BeforeEach
	public void before(final TestInfo testInfo) throws Exception
	{
		final IdeaTestFixtureFactory factory = IdeaTestFixtureFactory.getFixtureFactory();
		final IdeaProjectTestFixture testFixture =
			factory.createLightFixtureBuilder(EMPTY_PROJECT_DESCRIPTOR, testInfo.getDisplayName()).getFixture();
		this.fixture = factory.createCodeInsightFixture(testFixture, new LightTempDirTestFixtureImpl(true));
		this.fixture.setUp();
		this.fixture.setTestDataPath(this.getTestDataPath());
		this.storage = testFixture.getProject().getService(Storage.class);
	}
	
	@AfterEach
	public void after() throws Exception
	{
		this.fixture.tearDown();
		this.storage.clear();
	}
	
	void assertSaveAction(final ActionTestFile before, final ActionTestFile after)
	{
		this.fixture.configureByFile(before.getFilename());
		SaveActionManagerConstants.SAVE_ACTION_MANAGER.accept(this.fixture, SaveActionsServiceManager.getService());
		JUnit5Utils.rethrowAsJunit5Error(() -> this.fixture.checkResultByFile(after.getFilename()));
	}
	
	void assertSaveActionShortcut(final ActionTestFile before, final ActionTestFile after)
	{
		this.fixture.configureByFile(before.getFilename());
		ShortcutActionConstants.SAVE_ACTION_SHORTCUT_MANAGER.accept(this.fixture);
		JUnit5Utils.rethrowAsJunit5Error(() -> this.fixture.checkResultByFile(after.getFilename()));
	}
	
	void assertSaveActionBatch(final ActionTestFile before, final ActionTestFile after)
	{
		this.fixture.configureByFile(before.getFilename());
		BatchActionConstants.SAVE_ACTION_BATCH_MANAGER.accept(this.fixture);
		JUnit5Utils.rethrowAsJunit5Error(() -> this.fixture.checkResultByFile(after.getFilename()));
	}
	
	private String getTestDataPath()
	{
		/* See gradle config. Previous implementation not compatible with intellij gradle plugin >= 1.6.0 */
		final Path resources = Paths.get("./build/classes/java/resources");
		final Path root = Paths.get(resources.toString(), this.getClass().getPackage().getName().split("[.]"));
		return root.toString();
	}
}
