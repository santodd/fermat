package com.bitdubai.fermat_pip_plugin.layer.sub_app_module.notification.developer.bitdubai.version_1.event_handlers;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.Service;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.events.CompleteComponentRegistrationNotificationEvent;
import  com.bitdubai.fermat_api.layer.dmp_module.notification.NotificationType;
import com.bitdubai.fermat_pip_api.layer.module.notification.interfaces.NotificationManagerMiddleware;

/**
 * Created by Matias Furszyfer on 2015.08.18..
 */
public class CloudClientNotificationHandler implements FermatEventHandler {


    NotificationManagerMiddleware notificationManager;

    public CloudClientNotificationHandler(final NotificationManagerMiddleware notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Override
    public void handleEvent(FermatEvent fermatEvent) throws FermatException {

        CompleteComponentRegistrationNotificationEvent completeComponentRegistrationNotificationEvent =(CompleteComponentRegistrationNotificationEvent) fermatEvent;
        System.out.println("PROBANDO EVENTO MATI, PARA NOTIFICACIONES DE COMMUNICATION_CLOUD_CLIENT");

        if(completeComponentRegistrationNotificationEvent.getPlatformComponentProfileRegistered().getPlatformComponentType() == PlatformComponentType.COMMUNICATION_CLOUD_CLIENT) {

            System.out.println("PROBANDO EVENTO MATI, PARA NOTIFICACIONES DE ROBERT");

            if (((Service) this.notificationManager).getStatus() == ServiceStatus.STARTED) {

                //TODO: acá hay que implementar el add al pool de notificaciones
                //this.notificationManager.recordNavigationStructure(xmlText,link,filename,skinId);

                notificationManager.addNotificacion(NotificationType.CLOUD_CLIENT_CONNECTED);

            }
        }

    }
}
