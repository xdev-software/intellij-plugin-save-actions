package software.xdev.saveactions.core.action;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

import java.util.function.Consumer;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionUiKind;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;


public interface ShortcutActionConstants
{
	Consumer<CodeInsightTestFixture> SAVE_ACTION_SHORTCUT_MANAGER = fixture ->
		WriteCommandAction.writeCommandAction(fixture.getProject()).run(() -> runFixure(fixture));
	
	static void runFixure(final CodeInsightTestFixture fixture)
	{
		// set modification timestamp ++
		fixture.getFile().clearCaches();
		
		final ActionManager actionManager = ActionManager.getInstance();
		final AnAction action = actionManager.getAction(ShortcutAction.class.getName());
		
		final DataContext dataContext = SimpleDataContext.builder()
			.add(PROJECT, fixture.getProject())
			.add(PSI_FILE, fixture.getFile())
			.setParent(null)
			.build();
		
		// call plugin on document
		final AnActionEvent event = AnActionEvent.createEvent(
			dataContext,
			action.getTemplatePresentation().clone(),
			"save-actions",
			ActionUiKind.NONE,
			null);
		
		new ShortcutAction().actionPerformed(event);
	}
}
