package com.bitdubai.fermat_cht_plugin.layer.network_service.chat.developer.bitdubai.version_1.event_handlers;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.Service;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_api.layer.all_definition.network_service.interfaces.NetworkService;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.events.CompleteComponentConnectionRequestNotificationEvent;

/**
 * Created by Gabriel Araujo on 05/01/16.
 */
public class CompleteComponentConnectionRequestNotificationEventHandler implements FermatEventHandler {

    /*
    * Represent the networkService
    */
    private NetworkService networkService;

    /**
     * Constructor with parameter
     *
     * @param networkService
     */
    public CompleteComponentConnectionRequestNotificationEventHandler(NetworkService networkService) {
        this.networkService = networkService;
    }

    /**
     * (non-Javadoc)
     *
     * @see FermatEventHandler#handleEvent(FermatEvent)
     *
     * @param platformEvent
     * @throws Exception
     */
    @Override
    public void handleEvent(FermatEvent platformEvent) throws FermatException {

        // System.out.println("CompleteComponentConnectionRequestNotificationEventHandler - handleEvent platformEvent ="+platformEvent );


        if (((Service) this.networkService).getStatus() == ServiceStatus.STARTED) {

            CompleteComponentConnectionRequestNotificationEvent completeComponentConnectionRequestNotificationEvent = (CompleteComponentConnectionRequestNotificationEvent) platformEvent;


            if (completeComponentConnectionRequestNotificationEvent.getNetworkServiceTypeApplicant() == networkService.getNetworkServiceType()){

                /*
                 *  networkService make the job
                 */
                this.networkService.handleCompleteComponentConnectionRequestNotificationEvent(completeComponentConnectionRequestNotificationEvent.getApplicantComponent(), completeComponentConnectionRequestNotificationEvent.getRemoteComponent());

            }


        }
    }
}