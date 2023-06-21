package software.xdev.saveactions.core.service;

import software.xdev.saveactions.core.service.impl.SaveActionsDefaultService;
import software.xdev.saveactions.core.service.impl.SaveActionsJavaService;
import com.intellij.openapi.application.ApplicationManager;

/**
 * SaveActionsServiceManager is providing the concrete service implementation.
 * All actions are handled by the {@link SaveActionsService} implementation.
 *
 * @see SaveActionsDefaultService
 * @see SaveActionsJavaService
 */
public class SaveActionsServiceManager {


    private SaveActionsServiceManager() {
    }

    public static SaveActionsService getService() {
        return ServiceHandler.INSTANCE.getService();
    }

    private enum ServiceHandler {

        INSTANCE;

        private static SaveActionsService service;

        public SaveActionsService getService() {
            if (service == null) {
                newService();
            }
            return service;
        }

        private void newService() {
            service = ApplicationManager.getApplication().getService(SaveActionsJavaService.class);
            if (service == null) {
                service = ApplicationManager.getApplication().getService(SaveActionsDefaultService.class);
            }
        }
    }
}
