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
	private SaveActionsServiceManager()
	{
	}
	
	public static SaveActionsService getService()
	{
		return ServiceHandler.INSTANCE.getService();
	}
	
	private enum ServiceHandler
	{
		INSTANCE;
		
		private static SaveActionsService service;
		
		public SaveActionsService getService()
		{
			if(service == null)
			{
				this.newService();
			}
			return service;
		}
		
		private void newService()
		{
			service = ApplicationManager.getApplication().getService(SaveActionsJavaService.class);
			if(service == null)
			{
				service = ApplicationManager.getApplication().getService(SaveActionsDefaultService.class);
			}
		}
	}
}
