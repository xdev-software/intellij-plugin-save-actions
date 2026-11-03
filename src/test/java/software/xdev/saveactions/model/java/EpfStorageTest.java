package software.xdev.saveactions.model.java;

import static org.assertj.core.api.Assertions.assertThat;
import static software.xdev.saveactions.model.Action.activate;
import static software.xdev.saveactions.model.Action.explicitTypeCanBeDiamond;
import static software.xdev.saveactions.model.Action.fieldCanBeFinal;
import static software.xdev.saveactions.model.Action.localCanBeFinal;
import static software.xdev.saveactions.model.Action.missingOverrideAnnotation;
import static software.xdev.saveactions.model.Action.organizeImports;
import static software.xdev.saveactions.model.Action.rearrange;
import static software.xdev.saveactions.model.Action.reformat;
import static software.xdev.saveactions.model.Action.reformatChangedCode;
import static software.xdev.saveactions.model.Action.unnecessarySemicolon;
import static software.xdev.saveactions.model.Action.unqualifiedFieldAccess;
import static software.xdev.saveactions.model.Action.unqualifiedStaticMemberAccess;
import static software.xdev.saveactions.model.Action.useBlocks;
import static software.xdev.saveactions.model.java.EpfTestConstants.EXAMPLE_EPF_0;
import static software.xdev.saveactions.model.java.EpfTestConstants.EXAMPLE_EPF_1;
import static software.xdev.saveactions.model.java.EpfTestConstants.EXAMPLE_EPF_2;

import java.util.EnumSet;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import software.xdev.saveactions.model.Action;
import software.xdev.saveactions.model.Storage;


class EpfStorageTest
{
	
	private Storage storage;
	
	@BeforeEach
	void before()
	{
		this.storage = new Storage();
		
		this.storage.setActions(EnumSet.noneOf(Action.class));
		this.storage.setInclusions(new HashSet<>());
		this.storage.setExclusions(new HashSet<>());
		
		this.storage.getActions().add(activate);
		this.storage.getActions().add(reformat);
		this.storage.getActions().add(reformatChangedCode);
		this.storage.getActions().add(fieldCanBeFinal);
		this.storage.getActions().add(missingOverrideAnnotation);
		this.storage.getActions().add(unnecessarySemicolon);
		
		this.storage.getInclusions().add("inclusion1");
		this.storage.getInclusions().add("inclusion2");
		
		this.storage.getExclusions().add("exclusion1");
		this.storage.getExclusions().add("exclusion2");
	}
	
	@Test
	void should_storage_with_bad_configuration_path_returns_default_storage()
	{
		Storage epfStorage;
		
		epfStorage = EpfStorage.INSTANCE.getStorageOrDefault(this.storage.getConfigurationPath(), this.storage);
		assertThat(this.storage).isSameAs(epfStorage);
		
		this.storage.setConfigurationPath("bad path");
		epfStorage = EpfStorage.INSTANCE.getStorageOrDefault(this.storage.getConfigurationPath(), this.storage);
		assertThat(this.storage).isSameAs(epfStorage);
	}
	
	@Test
	void should_default_storage_values_are_copied_to_epf_storage_if_empty_file()
	{
		final Storage epfStorage = EpfStorage.INSTANCE.getStorageOrDefault(EXAMPLE_EPF_0.toString(), this.storage);
		assertThat(epfStorage).isNotSameAs(this.storage);
		
		assertThat(epfStorage.getActions()).isNotSameAs(this.storage.getActions());
		assertThat(epfStorage.getInclusions()).isNotSameAs(this.storage.getInclusions());
		assertThat(epfStorage.getExclusions()).isNotSameAs(this.storage.getExclusions());
		
		assertThat(epfStorage.getActions()).isEqualTo(this.storage.getActions());
		assertThat(epfStorage.getInclusions()).isEqualTo(this.storage.getInclusions());
		assertThat(epfStorage.getExclusions()).isEqualTo(this.storage.getExclusions());
	}
	
	@Test
	void should_storage_values_are_correct_for_file_format_1()
	{
		final Storage epfStorage = EpfStorage.INSTANCE.getStorageOrDefault(EXAMPLE_EPF_1.toString(), this.storage);
		assertThat(epfStorage).isNotSameAs(this.storage);
		
		final EnumSet<Action> expected = EnumSet.of(
			// from default store (not in java)
			activate,
			// in both store
			reformat,
			// added by java
			useBlocks,
			// added by java
			unqualifiedFieldAccess,
			// added by java
			localCanBeFinal,
			// added by java
			rearrange,
			// added by java
			organizeImports,
			// added by java
			explicitTypeCanBeDiamond,
			// added by java
			missingOverrideAnnotation,
			// from default store (not in java)
			unnecessarySemicolon
			// removed by java : fieldCanBeFinal
			// removed by java : reformatChangedCode
		);
		
		assertThat(epfStorage.getActions()).isEqualTo(expected);
		assertThat(epfStorage.getInclusions()).isEqualTo(this.storage.getInclusions());
		assertThat(epfStorage.getExclusions()).isEqualTo(this.storage.getExclusions());
	}
	
	@Test
	void should_storage_values_are_correct_for_file_format_2()
	{
		final Storage epfStorage = EpfStorage.INSTANCE.getStorageOrDefault(EXAMPLE_EPF_2.toString(), this.storage);
		assertThat(epfStorage).isNotSameAs(this.storage);
		
		final EnumSet<Action> expected = EnumSet.of(
			missingOverrideAnnotation,
			unnecessarySemicolon,
			rearrange,
			unqualifiedStaticMemberAccess,
			useBlocks,
			activate,
			organizeImports,
			unqualifiedFieldAccess,
			explicitTypeCanBeDiamond,
			localCanBeFinal,
			fieldCanBeFinal
		);
		
		assertThat(epfStorage.getActions()).isEqualTo(expected);
		assertThat(epfStorage.getInclusions()).isEqualTo(this.storage.getInclusions());
		assertThat(epfStorage.getExclusions()).isEqualTo(this.storage.getExclusions());
	}
}
