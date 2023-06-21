package software.xdev.saveactions.core.service.impl;

import software.xdev.saveactions.processors.BuildProcessor;
import software.xdev.saveactions.processors.GlobalProcessor;

import static software.xdev.saveactions.model.StorageFactory.DEFAULT;

/**
 * This ApplicationService implementation is used for all IDE flavors that are not handling JAVA.
 * <p/>
 * It is assigned as ExtensionPoint from inside plugin.xml. In terms of IDEs using Java this service is overridden
 * by the extended JAVA based version {@link SaveActionsJavaService}. Hence, it will not be loaded for Intellij IDEA,
 * Android Studio a.s.o.
 * <p/>
 * Services must be final classes as per definition. That is the reason to use an abstract class here.
 * <p/>
 *
 * @see AbstractSaveActionsService
 * @since 2.4.0
 */
public final class SaveActionsDefaultService extends AbstractSaveActionsService {

    public SaveActionsDefaultService() {
        super(DEFAULT);
        addProcessors(BuildProcessor.stream());
        addProcessors(GlobalProcessor.stream());
    }
}
