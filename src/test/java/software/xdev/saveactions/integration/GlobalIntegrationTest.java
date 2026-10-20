package software.xdev.saveactions.integration;

import static software.xdev.saveactions.model.Action.activate;
import static software.xdev.saveactions.model.Action.activateOnBatch;
import static software.xdev.saveactions.model.Action.activateOnShortcut;
import static software.xdev.saveactions.model.Action.organizeImports;
import static software.xdev.saveactions.model.Action.rearrange;
import static software.xdev.saveactions.model.Action.reformat;

import org.junit.jupiter.api.Test;


class GlobalIntegrationTest extends IntegrationTest
{
	@Test
	void should_reformat_without_activation_produces_same_file()
	{
		this.storage.setEnabled(reformat, true);
		this.assertSaveAction(ActionTestFile.Reformat_KO_Import_KO, ActionTestFile.Reformat_KO_Import_KO);
	}
	
	@Test
	void should_reformat_with_activation_produces_indented_file()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(reformat, true);
		this.assertSaveAction(ActionTestFile.Reformat_KO_Import_KO, ActionTestFile.Reformat_OK_Import_KO);
	}
	
	@Test
	void should_reformat_with_shortcut_produces_same_file()
	{
		this.storage.setEnabled(activateOnShortcut, true);
		this.storage.setEnabled(reformat, true);
		this.assertSaveAction(ActionTestFile.Reformat_KO_Import_KO, ActionTestFile.Reformat_KO_Import_KO);
	}
	
	@Test
	void should_reformat_with_shortcut_produces_indented_file_on_shortcut()
	{
		this.storage.setEnabled(activateOnShortcut, true);
		this.storage.setEnabled(reformat, true);
		this.assertSaveActionShortcut(ActionTestFile.Reformat_KO_Import_KO, ActionTestFile.Reformat_OK_Import_KO);
	}
	
	@Test
	void should_reformat_as_batch_produces_indented_file()
	{
		this.storage.setEnabled(activateOnBatch, true);
		this.storage.setEnabled(reformat, true);
		this.assertSaveActionBatch(ActionTestFile.Reformat_KO_Import_KO, ActionTestFile.Reformat_OK_Import_KO);
	}
	
	@Test
	void should_reformat_as_batch_on_shortcut_produces_same_file()
	{
		this.storage.setEnabled(activateOnShortcut, true);
		this.storage.setEnabled(reformat, true);
		this.assertSaveActionBatch(ActionTestFile.Reformat_KO_Import_KO, ActionTestFile.Reformat_KO_Import_KO);
	}
	
	@Test
	void should_import_without_activation_produces_same_file()
	{
		this.storage.setEnabled(organizeImports, true);
		this.assertSaveAction(ActionTestFile.Reformat_KO_Import_KO, ActionTestFile.Reformat_KO_Import_KO);
	}
	
	@Test
	void should_import_with_activation_produces_cleaned_import_file()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(organizeImports, true);
		this.assertSaveAction(ActionTestFile.Reformat_KO_Import_KO, ActionTestFile.Reformat_KO_Import_OK);
	}
	
	@Test
	void should_import_and_format_with_activation_produces_cleaned_import_and_formated_file()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(organizeImports, true);
		this.storage.setEnabled(reformat, true);
		this.assertSaveAction(ActionTestFile.Reformat_KO_Import_KO, ActionTestFile.Reformat_OK_Import_OK);
	}
	
	@Test
	void should_rearrange_without_activation_produces_same_file()
	{
		this.storage.setEnabled(rearrange, true);
		this.assertSaveAction(ActionTestFile.Reformat_KO_Import_KO, ActionTestFile.Reformat_KO_Import_KO);
	}
	
	@Test
	void should_rearrange_with_activation_produces_ordered_file()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(rearrange, true);
		this.assertSaveAction(ActionTestFile.Reformat_KO_Rearrange_KO, ActionTestFile.Reformat_KO_Rearrange_OK);
	}
	
	@Test
	void should_rearrange_and_format_with_activation_produces_ordered_file_and_formated_file()
	{
		this.storage.setEnabled(activate, true);
		this.storage.setEnabled(reformat, true);
		this.storage.setEnabled(rearrange, true);
		this.assertSaveAction(ActionTestFile.Reformat_KO_Rearrange_KO, ActionTestFile.Reformat_OK_Rearrange_OK);
	}
}
