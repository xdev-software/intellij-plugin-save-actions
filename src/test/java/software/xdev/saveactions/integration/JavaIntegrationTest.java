package software.xdev.saveactions.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
import static software.xdev.saveactions.model.Action.suppressAnnotation;
import static software.xdev.saveactions.model.Action.unnecessaryFinalOnLocalVariableOrParameter;
import static software.xdev.saveactions.model.Action.unnecessarySemicolon;
import static software.xdev.saveactions.model.Action.unnecessaryThis;
import static software.xdev.saveactions.model.Action.unqualifiedFieldAccess;
import static software.xdev.saveactions.model.Action.unqualifiedMethodAccess;
import static software.xdev.saveactions.model.Action.unqualifiedStaticMemberAccess;
import static software.xdev.saveactions.model.Action.useBlocks;

class JavaIntegrationTest extends IntegrationTest {

    @Test
    void should_fieldCanBeFinal_add_final_to_field() {
        storage.setEnabled(activate, true);
        storage.setEnabled(fieldCanBeFinal, true);
        assertSaveAction(ActionTestFile.FieldCanBeFinal_KO, ActionTestFile.FieldCanBeFinal_OK);
    }

    @Test
    void should_fieldCanBeFinal_add_final_to_field_on_shortcut() {
        storage.setEnabled(activateOnShortcut, true);
        storage.setEnabled(fieldCanBeFinal, true);
        assertSaveActionShortcut(ActionTestFile.FieldCanBeFinal_KO, ActionTestFile.FieldCanBeFinal_OK);
    }

    @Test
    void should_fieldCanBeFinal_add_final_to_field_on_batch() {
        storage.setEnabled(activateOnBatch, true);
        storage.setEnabled(fieldCanBeFinal, true);
        assertSaveActionBatch(ActionTestFile.FieldCanBeFinal_KO, ActionTestFile.FieldCanBeFinal_OK);
    }

    @Test
    void should_localCanBeFinal_add_final_to_local_variable_and_parameters() {
        storage.setEnabled(activate, true);
        storage.setEnabled(localCanBeFinal, true);
        assertSaveAction(ActionTestFile.LocalCanBeFinal_KO, ActionTestFile.LocalCanBeFinal_OK);
    }

    @Test
    void should_localCanBeFinalExceptImplicit_add_final_to_variable_but_not_resources() {
        storage.setEnabled(activate, true);
        storage.setEnabled(localCanBeFinalExceptImplicit, true);
        assertSaveAction(ActionTestFile.LocalCanBeFinalExceptImplicit_KO, ActionTestFile.LocalCanBeFinalExceptImplicit_OK);
    }

    @Test
    @Disabled("do not work")
    void should_methodMayBeStatic_add_static_keyword_to_method() {
        storage.setEnabled(activate, true);
        storage.setEnabled(methodMayBeStatic, true);
        assertSaveAction(ActionTestFile.MethodMayBeStatic_KO, ActionTestFile.MethodMayBeStatic_OK);
    }

    @Test
    void should_unqualifiedFieldAccess_add_this_to_field_access() {
        storage.setEnabled(activate, true);
        storage.setEnabled(unqualifiedFieldAccess, true);
        assertSaveAction(ActionTestFile.UnqualifiedFieldAccess_KO, ActionTestFile.UnqualifiedFieldAccess_OK);
    }

    @Test
    void should_unqualifiedMethodAccess_add_this_to_method_access() {
        storage.setEnabled(activate, true);
        storage.setEnabled(unqualifiedMethodAccess, true);
        assertSaveAction(ActionTestFile.UnqualifiedMethodAccess_KO, ActionTestFile.UnqualifiedMethodAccess_OK);
    }

    @Test
    void should_unqualifiedStaticMemberAccess_add_this_to_method_access() {
        storage.setEnabled(activate, true);
        storage.setEnabled(unqualifiedStaticMemberAccess, true);
        assertSaveAction(ActionTestFile.UnqualifiedStaticMemberAccess_KO, ActionTestFile.UnqualifiedStaticMemberAccess_OK);
    }

    @Test
    void should_customUnqualifiedStaticMemberAccess_add_this_to_method_access() {
        storage.setEnabled(activate, true);
        storage.setEnabled(customUnqualifiedStaticMemberAccess, true);
        assertSaveAction(ActionTestFile.CustomUnqualifiedStaticMemberAccess_KO, ActionTestFile.CustomUnqualifiedStaticMemberAccess_OK);
    }

    @Test
    void should_missingOverrideAnnotation_add_override_annotation() {
        storage.setEnabled(activate, true);
        storage.setEnabled(missingOverrideAnnotation, true);
        assertSaveAction(ActionTestFile.MissingOverrideAnnotation_KO, ActionTestFile.MissingOverrideAnnotation_OK);
    }

    @Test
    void should_useBlocks_add_blocks_to_if_else_while_for() {
        storage.setEnabled(activate, true);
        storage.setEnabled(useBlocks, true);
        assertSaveAction(ActionTestFile.UseBlocks_KO, ActionTestFile.UseBlocks_OK);
    }

    @Test
    @Disabled("do not work")
    void should_generateSerialVersionUID_generates_serial_version_uid_for_serializable_class() {
        storage.setEnabled(activate, true);
        storage.setEnabled(generateSerialVersionUID, true);
        assertSaveAction(ActionTestFile.GenerateSerialVersionUID_KO, ActionTestFile.GenerateSerialVersionUID_OK);
    }

    @Test
    void should_unnecessaryThis_removes_this_on_method_and_field() {
        storage.setEnabled(activate, true);
        storage.setEnabled(unnecessaryThis, true);
        assertSaveAction(ActionTestFile.UnnecessaryThis_KO, ActionTestFile.UnnecessaryThis_OK);
    }

    @Test
    void should_finalPrivateMethod_removes_final_on_private_methods() {
        storage.setEnabled(activate, true);
        storage.setEnabled(finalPrivateMethod, true);
        assertSaveAction(ActionTestFile.FinalPrivateMethod_KO, ActionTestFile.FinalPrivateMethod_OK);
    }

    @Test
    void should_unnecessaryFinalOnLocalVariableOrParameter_removes_final_on_local_varible_and_parameters() {
        storage.setEnabled(activate, true);
        storage.setEnabled(unnecessaryFinalOnLocalVariableOrParameter, true);
        assertSaveAction(ActionTestFile.UnnecessaryFinalOnLocalVariableOrParameter_KO, ActionTestFile.UnnecessaryFinalOnLocalVariableOrParameter_OK);
    }

    @Test
    @Disabled("do not work")
    void should_explicitTypeCanBeDiamond_removes_explicit_diamond() {
        storage.setEnabled(activate, true);
        storage.setEnabled(explicitTypeCanBeDiamond, true);
        assertSaveAction(ActionTestFile.ExplicitTypeCanBeDiamond_KO, ActionTestFile.ExplicitTypeCanBeDiamond_OK);
    }

    @Test
    void should_suppressAnnotation_remove_unnecessary_suppress_warning_annotation() {
        storage.setEnabled(activate, true);
        storage.setEnabled(suppressAnnotation, true);
        assertSaveAction(ActionTestFile.SuppressAnnotation_KO, ActionTestFile.SuppressAnnotation_OK);
    }

    @Test
    void should_unnecessarySemicolon_remove_unnecessary_semicolon() {
        storage.setEnabled(activate, true);
        storage.setEnabled(unnecessarySemicolon, true);
        assertSaveAction(ActionTestFile.UnnecessarySemicolon_KO, ActionTestFile.UnnecessarySemicolon_OK);
    }

    @Test
    void should_accessCanBeTightened_remove_unnecessary_semicolon() {
        storage.setEnabled(activate, true);
        storage.setEnabled(accessCanBeTightened, true);
        assertSaveAction(ActionTestFile.AccessCanBeTightened_KO, ActionTestFile.AccessCanBeTightened_OK);
    }

    @Test
    void should_singleStatementInBlock_remove_braces() {
        storage.setEnabled(activate, true);
        storage.setEnabled(singleStatementInBlock, true);
        assertSaveAction(ActionTestFile.SingleStatementInBlock_KO, ActionTestFile.SingleStatementInBlock_OK);
    }

    @Test
    void should_inspectionsAll_boogaloo() {
        storage.setEnabled(activate, true);
        storage.setEnabled(useBlocks, true);
        storage.setEnabled(accessCanBeTightened, true);
        storage.setEnabled(unnecessarySemicolon, true);
        assertSaveAction(ActionTestFile.InspectionsAll_KO, ActionTestFile.InspectionsAll_OK);
    }

}
