package software.xdev.saveactions.model;

import software.xdev.saveactions.model.java.EpfStorage;
import com.intellij.openapi.project.Project;

import java.util.function.Function;

public enum StorageFactory {

    DEFAULT(project -> project.getService(Storage.class)),

    JAVA(project -> {
        Storage defaultStorage = DEFAULT.getStorage(project);
        return EpfStorage.INSTANCE.getStorageOrDefault(defaultStorage.getConfigurationPath(), defaultStorage);
    });

    private final Function<Project, Storage> provider;

    StorageFactory(Function<Project, Storage> provider) {
        this.provider = provider;
    }

    public Storage getStorage(Project project) {
        return provider.apply(project);
    }

}
