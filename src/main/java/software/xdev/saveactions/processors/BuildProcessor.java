package software.xdev.saveactions.processors;

import static com.intellij.openapi.actionSystem.ActionPlaces.UNKNOWN;
import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT;
import static software.xdev.saveactions.utils.Helper.toVirtualFiles;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.ui.HotSwapUI;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionUiKind;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import software.xdev.saveactions.core.ExecutionMode;
import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import software.xdev.saveactions.model.Action;


/**
 * Available processors for build.
 */
@SuppressWarnings("java:S115")
public enum BuildProcessor implements Processor
{
	compile(
		Action.compile,
		(project, psiFiles) -> () -> {
			if(!SaveActionsServiceManager.getService().isCompilingAvailable())
			{
				return;
			}
			CompilerManager.getInstance(project).compile(toVirtualFiles(psiFiles), null);
		}),
	
	reload(
		Action.reload,
		(project, psiFiles) -> () -> {
			if(!SaveActionsServiceManager.getService().isCompilingAvailable())
			{
				return;
			}
			DebuggerManagerEx debuggerManager = DebuggerManagerEx.getInstanceEx(project);
			DebuggerSession session = debuggerManager.getContext().getDebuggerSession();
			if(session != null && session.isAttached())
			{
				HotSwapUI.getInstance(project).reloadChangedClasses(session, true);
			}
		}),
	
	executeAction(
		Action.executeAction,
		(project, psiFiles) -> () -> {
			ActionManager actionManager = ActionManager.getInstance();
			
			List<String> actionIds = SaveActionsServiceManager.getService().getQuickLists(project).stream()
				.flatMap(quickList -> Arrays.stream(quickList.getActionIds()))
				.toList();
			
			for(String actionId : actionIds)
			{
				AnAction anAction = actionManager.getAction(actionId);
				if(anAction == null)
				{
					continue;
				}
				DataContext dataContext = SimpleDataContext.builder()
					.add(PROJECT, project)
					.add(EDITOR, FileEditorManager.getInstance(project).getSelectedTextEditor())
					.setParent(null)
					.build();
				AnActionEvent event = AnActionEvent.createEvent(
					dataContext,
					anAction.getTemplatePresentation(),
					UNKNOWN,
					ActionUiKind.NONE,
					null);
				
				anAction.actionPerformed(event);
			}
		})
		{
			@Override
			public SaveCommand getSaveCommand(final Project project, final Set<PsiFile> psiFiles)
			{
				return new SaveReadCommand(project, psiFiles, this.getModes(), this.getAction(), this.getCommand());
			}
		};
	
	private final Action action;
	private final BiFunction<Project, PsiFile[], Runnable> command;
	
	BuildProcessor(final Action action, final BiFunction<Project, PsiFile[], Runnable> command)
	{
		this.action = action;
		this.command = command;
	}
	
	@Override
	public Action getAction()
	{
		return this.action;
	}
	
	@Override
	public Set<ExecutionMode> getModes()
	{
		return EnumSet.allOf(ExecutionMode.class);
	}
	
	@Override
	public int getOrder()
	{
		return 2;
	}
	
	@Override
	public SaveCommand getSaveCommand(final Project project, final Set<PsiFile> psiFiles)
	{
		return new SaveWriteCommand(project, psiFiles, this.getModes(), this.getAction(), this.getCommand());
	}
	
	public BiFunction<Project, PsiFile[], Runnable> getCommand()
	{
		return this.command;
	}
	
	public static Optional<Processor> getProcessorForAction(final Action action)
	{
		return stream().filter(processor -> processor.getAction().equals(action)).findFirst();
	}
	
	public static Stream<Processor> stream()
	{
		return Arrays.stream(values());
	}
}
