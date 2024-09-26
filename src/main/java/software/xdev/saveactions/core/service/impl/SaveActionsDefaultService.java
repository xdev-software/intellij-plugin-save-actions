package software.xdev.saveactions.core.service.impl;

import static software.xdev.saveactions.model.StorageFactory.DEFAULT;

import software.xdev.saveactions.processors.BuildProcessor;
import software.xdev.saveactions.processors.GlobalProcessor;


/**
 * This ApplicationService implementation is used for all IDE flavors that are not handling JAVA.
 * <p/>
 * It is assigned as ExtensionPoint from inside plugin.xml. In terms of IDEs using Java this service is overridden by
 * the extended JAVA based version {@link SaveActionsJavaService}. Hence, it will not be loaded for Intellij IDEA,
 * Android Studio a.s.o.
 * <p/>
 * Services must be final classes as per definition. That is the reason to use an abstract class here.
 * <p/>
 *
 * @see AbstractSaveActionsService
 */
public final class SaveActionsDefaultService extends AbstractSaveActionsService
{
	public SaveActionsDefaultService()
	{
		super(DEFAULT);
		this.addProcessors(BuildProcessor.stream());
		this.addProcessors(GlobalProcessor.stream());
	}
}
