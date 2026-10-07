package software.xdev.saveactions.integration;

import static software.xdev.saveactions.model.Action.accessCanBeTightened;
import static software.xdev.saveactions.model.Action.activate;
import static software.xdev.saveactions.model.Action.activateOnBatch;
import static software.xdev.saveactions.model.Action.activateOnShortcut;
import static software.xdev.saveactions.model.Action.customUnqualifiedStaticMemberAccess;
import static software.xdev.saveactions.model.Action.explicitTypeCanBeDiamond;
import static software.xdev.saveactions.model.Action.fieldCanBeFinal;
import static software.xdev.saveactions.model.Action.finalPrivateMethod;
import static software.xdev.saveactions.model.Action.generateSerialVersionUID;
import static software.xdev.saveactions.model.Action.localCanBeFinal;
import static software.xdev.saveactions.model.Action.localCanBeFinalExceptImplicit;
import static software.xdev.saveactions.model.Action.methodMayBeStatic;
import static software.xdev.saveactions.model.Action.missingOverrideAnnotation;
import static software.xdev.saveactions.model.Action.singleStatementInBlock;
import static software.xdev.saveactions.model.Action.unnecessaryFinalOnLocalVariableOrParameter;
import static software.xdev.saveactions.model.Action.unnecessarySemicolon;
import static software.xdev.saveactions.model.Action.unnecessaryThis;
import static software.xdev.saveactions.model.Action.unqualifiedFieldAccess;
import static software.xdev.saveactions.model.Action.unqualifiedMethodAccess;
import static software.xdev.saveactions.model.Action.unqualifiedStaticMemberAccess;
import static software.xdev.saveactions.model.Action.useBlocks;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


class JavaIntegrationTest extends IntegrationTest
{
	@Test
	void should_fieldCanBeFinal_add_final_to_field()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(fieldCanBeFinal, true);
		this.assertSaveAction(ActionTestFile.FieldCanBeFinal_KO, ActionTestFile.FieldCanBeFinal_OK);
	}
	
	@Test
	void should_fieldCanBeFinal_add_final_to_field_on_shortcut()
	{
		this.storage.setEnabled(activateOnShortcut, true);
		this.storage.setEnabled(fieldCanBeFinal, true);
		this.assertSaveActionShortcut(ActionTestFile.FieldCanBeFinal_KO, ActionTestFile.FieldCanBeFinal_OK);
	}
	
	@Test
	void should_fieldCanBeFinal_add_final_to_field_on_batch()
	{
		this.storage.setEnabled(activateOnBatch, true);
		this.storage.setEnabled(fieldCanBeFinal, true);
		this.assertSaveActionBatch(ActionTestFile.FieldCanBeFinal_KO, ActionTestFile.FieldCanBeFinal_OK);
	}
	
	@Test
	void should_localCanBeFinal_add_final_to_local_variable_and_parameters()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(localCanBeFinal, true);
		this.assertSaveAction(ActionTestFile.LocalCanBeFinal_KO, ActionTestFile.LocalCanBeFinal_OK);
	}
	
	@Test
	void should_localCanBeFinalExceptImplicit_add_final_to_variable_but_not_resources()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(localCanBeFinalExceptImplicit, true);
		this.assertSaveAction(
			ActionTestFile.LocalCanBeFinalExceptImplicit_KO,
			ActionTestFile.LocalCanBeFinalExceptImplicit_OK);
	}
	
	@Test
	@Disabled("do not work")
	void should_methodMayBeStatic_add_static_keyword_to_method()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(methodMayBeStatic, true);
		this.assertSaveAction(ActionTestFile.MethodMayBeStatic_KO, ActionTestFile.MethodMayBeStatic_OK);
	}
	
	@Test
	void should_unqualifiedFieldAccess_add_this_to_field_access()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(unqualifiedFieldAccess, true);
		this.assertSaveAction(ActionTestFile.UnqualifiedFieldAccess_KO, ActionTestFile.UnqualifiedFieldAccess_OK);
	}
	
	@Test
	void should_unqualifiedMethodAccess_add_this_to_method_access()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(unqualifiedMethodAccess, true);
		this.assertSaveAction(ActionTestFile.UnqualifiedMethodAccess_KO, ActionTestFile.UnqualifiedMethodAccess_OK);
	}
	
	@Test
	void should_unqualifiedStaticMemberAccess_add_this_to_method_access()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(unqualifiedStaticMemberAccess, true);
		this.assertSaveAction(
			ActionTestFile.UnqualifiedStaticMemberAccess_KO,
			ActionTestFile.UnqualifiedStaticMemberAccess_OK);
	}
	
	@Test
	void should_customUnqualifiedStaticMemberAccess_add_this_to_method_access()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(customUnqualifiedStaticMemberAccess, true);
		this.assertSaveAction(
			ActionTestFile.CustomUnqualifiedStaticMemberAccess_KO,
			ActionTestFile.CustomUnqualifiedStaticMemberAccess_OK);
	}
	
	@Test
	void should_missingOverrideAnnotation_add_override_annotation()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(missingOverrideAnnotation, true);
		this.assertSaveAction(ActionTestFile.MissingOverrideAnnotation_KO, ActionTestFile.MissingOverrideAnnotation_OK);
	}
	
	@Test
	void should_useBlocks_add_blocks_to_if_else_while_for()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(useBlocks, true);
		this.assertSaveAction(ActionTestFile.UseBlocks_KO, ActionTestFile.UseBlocks_OK);
	}
	
	@Test
	@Disabled("do not work")
	void should_generateSerialVersionUID_generates_serial_version_uid_for_serializable_class()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(generateSerialVersionUID, true);
		this.assertSaveAction(ActionTestFile.GenerateSerialVersionUID_KO, ActionTestFile.GenerateSerialVersionUID_OK);
	}
	
	@Test
	void should_unnecessaryThis_removes_this_on_method_and_field()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(unnecessaryThis, true);
		this.assertSaveAction(ActionTestFile.UnnecessaryThis_KO, ActionTestFile.UnnecessaryThis_OK);
	}
	
	@Test
	void should_finalPrivateMethod_removes_final_on_private_methods()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(finalPrivateMethod, true);
		this.assertSaveAction(ActionTestFile.FinalPrivateMethod_KO, ActionTestFile.FinalPrivateMethod_OK);
	}
	
	@Test
	void should_unnecessaryFinalOnLocalVariableOrParameter_removes_final_on_local_varible_and_parameters()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(unnecessaryFinalOnLocalVariableOrParameter, true);
		this.assertSaveAction(
			ActionTestFile.UnnecessaryFinalOnLocalVariableOrParameter_KO,
			ActionTestFile.UnnecessaryFinalOnLocalVariableOrParameter_OK);
	}
	
	@Test
	@Disabled("do not work")
	void should_explicitTypeCanBeDiamond_removes_explicit_diamond()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(explicitTypeCanBeDiamond, true);
		this.assertSaveAction(ActionTestFile.ExplicitTypeCanBeDiamond_KO, ActionTestFile.ExplicitTypeCanBeDiamond_OK);
	}
	
	@Test
	void should_unnecessarySemicolon_remove_unnecessary_semicolon()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(unnecessarySemicolon, true);
		this.assertSaveAction(ActionTestFile.UnnecessarySemicolon_KO, ActionTestFile.UnnecessarySemicolon_OK);
	}
	
	@Test
	void should_accessCanBeTightened_remove_unnecessary_semicolon()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(accessCanBeTightened, true);
		this.assertSaveAction(ActionTestFile.AccessCanBeTightened_KO, ActionTestFile.AccessCanBeTightened_OK);
	}
	
	@Test
	void should_singleStatementInBlock_remove_braces()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(singleStatementInBlock, true);
		this.assertSaveAction(ActionTestFile.SingleStatementInBlock_KO, ActionTestFile.SingleStatementInBlock_OK);
	}
	
	@Test
	void should_inspectionsAll_boogaloo()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(useBlocks, true);
		this.storage.setEnabled(accessCanBeTightened, true);
		this.storage.setEnabled(unnecessarySemicolon, true);
		this.assertSaveAction(ActionTestFile.InspectionsAll_KO, ActionTestFile.InspectionsAll_OK);
	}
}
