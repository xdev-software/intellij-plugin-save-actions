package software.xdev.saveactions.model;

import static software.xdev.saveactions.model.ActionType.activation;
import static software.xdev.saveactions.model.ActionType.build;
import static software.xdev.saveactions.model.ActionType.global;
import static software.xdev.saveactions.model.ActionType.java;

import java.util.Arrays;
import java.util.stream.Stream;


@SuppressWarnings("java:S115")
public enum Action
{
	// Activation
	activate("Activate save actions on save (before saving each file, performs the configured actions below)",
		activation),
	
	activateOnShortcut("Activate save actions on shortcut (default \"CTRL + SHIFT + S\")",
		activation),
	
	activateOnBatch("Activate save actions on batch (\"Code > Save Actions > Execute on multiple files\")",
		activation),
	
	noActionIfCompileErrors("No action if compile errors (applied per file)",
		activation),
	
	processAsync("Process files asynchronously "
		+ "(will result in less UI hangs but may break if a processor needs the UI)",
		activation),
	
	// Global
	organizeImports("Optimize imports",
		global),
	
	reformat("Reformat file",
		global),
	
	reformatChangedCode("Reformat only changed code (only if VCS configured)",
		global),
	
	rearrange("Rearrange fields and methods "
		+ "(configured in \"File > Settings > Editor > Code Style > (...) > Arrangement\")",
		global),
	
	// Build
	compile("[experimental] Compile files (using \"Build > Build Project\")",
		build),
	
	reload("[experimental] Reload files in running debugger (using \"Run > Reload Changed Classes\")",
		build),
	
	executeAction("[experimental] Execute an action (using quick lists at "
		+ "\"File > Settings > Appearance & Behavior > Quick Lists\")",
		build),
	
	// Java fixes
	fieldCanBeFinal("Add final modifier to field",
		java),
	
	localCanBeFinal("Add final modifier to local variable or parameter",
		java),
	
	localCanBeFinalExceptImplicit("Add final modifier to local variable or parameter except if it is implicit",
		java),
	
	methodMayBeStatic("Add static modifier to methods",
		java),
	
	unqualifiedFieldAccess("Add this to field access",
		java),
	
	unqualifiedMethodAccess("Add this to method access",
		java),
	
	unqualifiedStaticMemberAccess("Add class qualifier to static member access",
		java),
	
	customUnqualifiedStaticMemberAccess("Add class qualifier to static member access outside declaring class",
		java),
	
	missingOverrideAnnotation("Add missing @Override annotations",
		java),
	
	useBlocks("Add blocks to if/while/for statements",
		java),
	
	generateSerialVersionUID("Add a serialVersionUID field for Serializable classes",
		java),
	
	unnecessaryThis("Remove unnecessary this to field and method",
		java),
	
	finalPrivateMethod("Remove final from private method",
		java),
	
	unnecessaryFinalOnLocalVariableOrParameter("Remove unnecessary final for local variable or parameter",
		java),
	
	explicitTypeCanBeDiamond("Remove explicit generic type for diamond",
		java),
	
	unnecessarySemicolon("Remove unnecessary semicolon",
		java),
	
	singleStatementInBlock("Remove blocks from if/while/for statements",
		java),
	
	accessCanBeTightened("Change visibility of field or method to lower access",
		java);
	
	private final String text;
	private final ActionType type;
	
	Action(final String text, final ActionType type)
	{
		this.text = text;
		this.type = type;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public ActionType getType()
	{
		return this.type;
	}
	
	public static Stream<Action> stream()
	{
		return Arrays.stream(values());
	}
	
	public static Stream<Action> stream(final ActionType type)
	{
		return Arrays.stream(values()).filter(action -> action.type.equals(type));
	}
}
