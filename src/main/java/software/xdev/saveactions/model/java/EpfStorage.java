package software.xdev.saveactions.model.java;

import static java.util.Collections.emptyList;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.intellij.openapi.diagnostic.Logger;

import software.xdev.saveactions.core.service.SaveActionsService;
import software.xdev.saveactions.model.Action;
import software.xdev.saveactions.model.Storage;


/**
 * Storage implementation for the Workspace-Mechanic-Format. Only the Java language-specific actions are supported.
 * <p>
 * The main method {@link #getStorageOrDefault(String, Storage)} return a configuration based on EPF if the path to EPF
 * configuration file is set and valid, or else the default configuration is returned.
 * <p>
 * The default storage is used to copy the actions, the inclusions and the exclusions, then the actions are taken from
 * the epf file and overrides the actions precedently set.
 *
 * @author markiewb
 */
public enum EpfStorage
{
	INSTANCE;
	
	private static final Logger LOGGER = Logger.getInstance(SaveActionsService.class);
	
	public Storage getStorageOrDefault(final String configurationPath, final Storage defaultStorage)
	{
		try
		{
			return this.getStorageOrDefault0(configurationPath, defaultStorage);
		}
		catch(final IOException e)
		{
			LOGGER.info("Error in configuration file " + defaultStorage.getConfigurationPath(), e);
			return defaultStorage;
		}
	}
	
	private Storage getStorageOrDefault0(final String configurationPath, final Storage defaultStorage)
		throws IOException
	{
		if("".equals(configurationPath) || configurationPath == null)
		{
			return defaultStorage;
		}
		final Storage storage = new Storage(defaultStorage);
		final Properties properties = this.readProperties(configurationPath);
		Action.stream().forEach(action -> storage.setEnabled(action, this.isEnabledInEpf(properties, action)
			.orElse(defaultStorage.isEnabled(action))));
		return storage;
	}
	
	private Optional<Boolean> isEnabledInEpf(final Properties properties, final Action action)
	{
		final List<EpfKey> epfKeys = EpfAction.getEpfActionForAction(action)
			.map(EpfAction::getEpfKeys)
			.orElse(emptyList());
		for(final EpfKey epfKey : epfKeys)
		{
			if(this.isEnabledInEpf(properties, epfKey, true))
			{
				return Optional.of(true);
			}
			if(this.isEnabledInEpf(properties, epfKey, false))
			{
				return Optional.of(false);
			}
		}
		return Optional.empty();
	}
	
	private boolean isEnabledInEpf(final Properties properties, final EpfKey key, final boolean value)
	{
		return EpfKey.getPrefixes().stream()
			.anyMatch(prefix -> String.valueOf(value).equals(properties.getProperty(prefix + "." + key)));
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
