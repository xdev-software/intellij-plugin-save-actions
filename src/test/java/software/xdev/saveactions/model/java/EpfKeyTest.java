package software.xdev.saveactions.model.java;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Test;


class EpfKeyTest
{
	@Test
	void should_all_example_file_0_keys_be_present_in_epf_key() throws IOException
	{
		final Properties properties = this.readProperties(EpfTestConstants.EXAMPLE_EPF_0.toString());
		this.assertPropertyPresenceInEpf(properties);
	}
	
	@Test
	void should_all_example_file_1_keys_be_present_in_epf_key() throws IOException
	{
		final Properties properties = this.readProperties(EpfTestConstants.EXAMPLE_EPF_1.toString());
		this.assertPropertyPresenceInEpf(properties);
	}
	
	@Test
	void should_all_example_file_2_keys_be_present_in_epf_key() throws IOException
	{
		final Properties properties = this.readProperties(EpfTestConstants.EXAMPLE_EPF_2.toString());
		this.assertPropertyPresenceInEpf(properties);
	}
	
	@Test
	void should_all_epf_key_be_present_in_example_files_2_to_remove_unused_keys() throws IOException
	{
		final Properties properties = this.readProperties(EpfTestConstants.EXAMPLE_EPF_2.toString());
		final List<String> epfKeyNames = this.getEpfKeyNames();
		final List<String> propertiesKeyNames = this.getPropertiesKeyNames(properties);
		epfKeyNames.forEach(epfKeyName -> assertThat(propertiesKeyNames).contains(epfKeyName));
	}
	
	private void assertPropertyPresenceInEpf(final Properties properties)
	{
		final List<String> epfKeyNames = this.getEpfKeyNames();
		final List<String> propertiesKeyNames = this.getPropertiesKeyNames(properties);
		propertiesKeyNames.forEach(propertiesKeyName -> assertThat(epfKeyNames).contains(propertiesKeyName));
	}
	
	private List<String> getPropertiesKeyNames(final Properties properties)
	{
		return properties.keySet().stream()
			.map(Object::toString)
			.filter(key -> EpfKey.getPrefixes().stream().anyMatch(key::startsWith))
			.map(key -> key.substring(key.lastIndexOf(".") == -1 ? 0 : key.lastIndexOf(".") + 1))
			.collect(toList());
	}
	
	private List<String> getEpfKeyNames()
	{
		return EpfKey.stream()
			.map(EpfKey::name)
			.collect(toList());
	}
	
	private Properties readProperties(final String configurationPath) throws IOException
	{
		final Properties properties = new Properties();
		try(final FileInputStream in = new FileInputStream(configurationPath))
		{
			properties.load(in);
		}
		return properties;
	}
}
