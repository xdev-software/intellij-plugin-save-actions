package software.xdev.saveactions.model;

import java.util.function.Function;

import com.intellij.openapi.project.Project;

import software.xdev.saveactions.model.java.EpfStorage;


public enum StorageFactory
{
	DEFAULT(project -> project.getService(Storage.class)),
	
	JAVA(project -> {
		Storage defaultStorage = DEFAULT.getStorage(project);
		return EpfStorage.INSTANCE.getStorageOrDefault(defaultStorage.getConfigurationPath(), defaultStorage);
	});
	
	private final Function<Project, Storage> provider;
	
	StorageFactory(final Function<Project, Storage> provider)
	{
		this.provider = provider;
	}
	
	public Storage getStorage(final Project project)
	{
		return this.provider.apply(project);
	}
}
