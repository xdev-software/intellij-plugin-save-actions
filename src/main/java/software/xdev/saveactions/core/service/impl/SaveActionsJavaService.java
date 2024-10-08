package software.xdev.saveactions.core.service.impl;

import static software.xdev.saveactions.model.StorageFactory.JAVA;

import software.xdev.saveactions.processors.BuildProcessor;
import software.xdev.saveactions.processors.GlobalProcessor;
import software.xdev.saveactions.processors.java.JavaProcessor;


/**
 * This ApplicationService implementation is used for all JAVA based IDE flavors.
 * <p/>
 * It is assigned as ExtensionPoint from inside plugin-java.xml and overrides the default implementation
 * {@link SaveActionsDefaultService} which is not being loaded for Intellij IDEA, Android Studio a.s.o. Instead this
 * implementation will be assigned. Thus, all processors have to be configured by this class as well.
 * <p/>
 * Services must be final classes as per definition. That is the reason to use an abstract class here.
 * <p/>
 *
 * @see AbstractSaveActionsService
 */
public final class SaveActionsJavaService extends AbstractSaveActionsService
{
	public SaveActionsJavaService()
	{
		super(JAVA);
		this.addProcessors(BuildProcessor.stream());
		this.addProcessors(GlobalProcessor.stream());
		this.addProcessors(JavaProcessor.stream());
	}
}
