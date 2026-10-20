package software.xdev.saveactions.core.service;

import com.intellij.openapi.application.ApplicationManager;

import software.xdev.saveactions.core.service.impl.SaveActionsDefaultService;
import software.xdev.saveactions.core.service.impl.SaveActionsJavaService;


/**
 * SaveActionsServiceManager is providing the concrete service implementation. All actions are handled by the
 * {@link SaveActionsService} implementation.
 *
 * @see SaveActionsDefaultService
 * @see SaveActionsJavaService
 */
public final class SaveActionsServiceManager
{
	static SaveActionsService instance;
	
	public static SaveActionsService getService()
	{
		if(instance == null)
		{
			initService();
		}
		return instance;
	}
	
	private static synchronized void initService()
	{
		if(instance != null)
		{
			return;
		}
		
		instance = ApplicationManager.getApplication().getService(SaveActionsJavaService.class);
		if(instance == null)
		{
			instance = ApplicationManager.getApplication().getService(SaveActionsDefaultService.class);
		}
	}
	
	private SaveActionsServiceManager()
	{
	}
}
