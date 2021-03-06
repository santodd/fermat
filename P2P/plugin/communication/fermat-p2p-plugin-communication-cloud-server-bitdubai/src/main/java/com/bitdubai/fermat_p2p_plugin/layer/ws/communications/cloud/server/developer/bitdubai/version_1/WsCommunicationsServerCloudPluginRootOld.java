/*
 * @#WsCommunicationsServerCloudPluginRoot.java - 2015
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.Plugin;
import com.bitdubai.fermat_api.Service;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.FermatManager;
import com.bitdubai.fermat_api.layer.all_definition.developer.LogManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_api.layer.osa_android.file_system.DealsWithPluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.DealsWithLogger;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogLevel;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogManager;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.WsCommunicationCloudServer;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.WsCommunicationsCloudServerPingAgent;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.processors.ActorUpdateRequestPacketProcessor;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.processors.ComponentConnectionRequestPacketProcessor;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.processors.ComponentRegistrationRequestPacketProcessor;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.processors.DiscoveryComponentConnectionRequestPacketProcessor;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.processors.RequestListComponentRegisterPacketProcessor;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.webservices.RestletCommunicationCloudServer;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.DealsWithErrors;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.DealsWithEvents;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocketImpl;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;


/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.WsCommunicationsServerCloudPluginRoot</code> is
 * the responsible to initialize all component to work together, and hold all resources they needed.
 * <p/>
 *
 * Created by Roberto Requena - (rart3001@gmail.com) on 03/09/15.
 *
 * @version 1.0
 */
public class WsCommunicationsServerCloudPluginRootOld implements Service, DealsWithEvents,DealsWithLogger, LogManagerForDevelopers, DealsWithErrors, DealsWithPluginFileSystem,Plugin {

    /**
     * Represent the logger instance
     */
    private Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(WsCommunicationsServerCloudPluginRootOld.class));

    /**
     * Represents the value of DISABLE_SERVER
     */
    public static final Boolean DISABLE_SERVER = Boolean.TRUE;

    /**
     * Represents the value of ENABLE_SERVER
     */
    public static final Boolean ENABLE_SERVER = Boolean.FALSE;

    /**
     * Represent the status of this service
     */
    private ServiceStatus serviceStatus = ServiceStatus.CREATED;

    /**
     * Represent the errorManager
     */
    private ErrorManager errorManager;

    /**
     * Represent the eventManager
     */
    private EventManager eventManager;

    /**
     * Represent the logManager
     */
    private LogManager logManager;

    /**
     * Represent the newLoggingLevel
     */
    static Map<String, LogLevel> newLoggingLevel = new HashMap<String, LogLevel>();

    /**
     * Hold the list of event listeners
     */
    private List<FermatEventListener> listenersAdded = new ArrayList<>();

    /**
     * Represent the wsCommunicationCloudServer
     */
    private WsCommunicationCloudServer wsCommunicationCloudServer;

    /**
     * DealsWithPluginIdentity Interface member variables.
     */
    private UUID pluginId;

    /**
     * Represent the disableServerFlag
     */
    private Boolean disableServerFlag;

    /**
     * Represent the  wsCommunicationsCloudServerPingAgent
     */
    private WsCommunicationsCloudServerPingAgent wsCommunicationsCloudServerPingAgent;

    /**
     * Constructor
     */
    public WsCommunicationsServerCloudPluginRootOld(){
        super();
        this.disableServerFlag = WsCommunicationsServerCloudPluginRootOld.DISABLE_SERVER;
    }

    /**
     * This method validate is all required resource are injected into
     * the plugin root by the platform
     *
     * @throws CantStartPluginException
     */
    private void validateInjectedResources() throws CantStartPluginException {

         /*
         * If all resources are inject
         */
        if (eventManager == null            ||
                logManager  == null         ||
                    errorManager == null) {

            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("logManager: " + logManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);

            LOG.error("No all required resource are injected");
            LOG.error("contextBuffer = "+contextBuffer);

            String context = contextBuffer.toString();
            String possibleCause = "No all required resource are injected";
            CantStartPluginException pluginStartException = new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);

            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_WS_COMMUNICATION_CLIENT_CHANNEL, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
            throw pluginStartException;

        }

    }

    @Override
    public FermatManager getManager() {
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see Service#start()
     */
    @Override
    public void start() {

        try {

            /*
             * Validate required resources
             */
           // validateInjectedResources();

            if (disableServerFlag) {
                LOG.info("Local Server is Disable, no started");
                return;
            }

            LOG.info("Starting plugin");

            /*
             * Get all network interfaces of the device
             */
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {

                NetworkInterface networkInterface = interfaces.nextElement();

                /**
                 * If not a loopback interfaces (127.0.0.1) and is active
                 */
                if (!networkInterface.isLoopback() && networkInterface.isUp()){

                    /*
                     * Get his inet addresses
                     */
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                    /*
                     * Create a cloud service for each ip
                     */
                    for (InetAddress address : Collections.list(addresses)) {

                        /**
                         * look only for ipv4 addresses
                         */
                         if (address instanceof Inet6Address) {
                             continue;
                         }

                        WebSocketImpl.DEBUG = false;
                        InetSocketAddress inetSocketAddress = new InetSocketAddress(address, WsCommunicationCloudServer.DEFAULT_PORT);
                        wsCommunicationCloudServer = new WsCommunicationCloudServer(inetSocketAddress);
                        wsCommunicationCloudServer.registerFermatPacketProcessor(new ComponentRegistrationRequestPacketProcessor());
                        wsCommunicationCloudServer.registerFermatPacketProcessor(new ComponentConnectionRequestPacketProcessor());
                        wsCommunicationCloudServer.registerFermatPacketProcessor(new DiscoveryComponentConnectionRequestPacketProcessor());
                        wsCommunicationCloudServer.registerFermatPacketProcessor(new RequestListComponentRegisterPacketProcessor());
                        wsCommunicationCloudServer.registerFermatPacketProcessor(new ActorUpdateRequestPacketProcessor());
                        wsCommunicationCloudServer.start();

                        /*
                         * Start the ping agent
                         */
                        wsCommunicationsCloudServerPingAgent = new WsCommunicationsCloudServerPingAgent(wsCommunicationCloudServer);
                        wsCommunicationsCloudServerPingAgent.start();

                        LOG.info("New CommunicationChannelAddress linked on " + networkInterface.getName());
                        LOG.info("Host = " + inetSocketAddress.getHostString());
                        LOG.info("Port = "     + inetSocketAddress.getPort());
                        LOG.info("Communication Service Manager on " + networkInterface.getName() + " started.");

                        break;

                    }

                }

            }

            /*
             * Create and start the restlet server
             */
            RestletCommunicationCloudServer rlCommunicationCloudServer = new RestletCommunicationCloudServer(wsCommunicationCloudServer);
            rlCommunicationCloudServer.start();

            /*
             * Start the socket to handle the request of Component Registered List
             */
           // SocketServerInitialization socketServerInitialization =new SocketServerInitialization(wsCommunicationCloudServer);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        /*
         * Set the new status of the service
         */
        this.serviceStatus = ServiceStatus.STARTED;

    }

    /**
     * (non-Javadoc)
     *
     * @see Service#pause()
     */
    @Override
    public void pause() {
        this.serviceStatus = ServiceStatus.PAUSED;
    }

    /**
     * (non-Javadoc)
     *
     * @see Service#resume()
     */
    @Override
    public void resume() {
        this.serviceStatus = ServiceStatus.STARTED;
    }

    /**
     * (non-Javadoc)
     *
     * @see Service#stop()
     */
    @Override
    public void stop() {

        /*
         * Remove all the event listeners registered with the event manager.
         */
        for (FermatEventListener fermatEventListener : listenersAdded) {
            eventManager.removeListener(fermatEventListener);
        }

        /*
         * Clear the list
         */
        listenersAdded.clear();

        /*
         * Change the estatus
         */
        this.serviceStatus = ServiceStatus.STOPPED;

    }

    /**
     * (non-Javadoc)
     *
     * @see Service#getStatus()
     */
    @Override
    public ServiceStatus getStatus() {
        return this.serviceStatus;
    }


    /**
     * (non-Javadoc)
     *
     * @see DealsWithLogger#setLogManager(LogManager)
     */
    @Override
    public void setLogManager(LogManager logManager) {
        this.logManager = logManager;
    }

    /**
     * (non-Javadoc)
     *
     * @see LogManagerForDevelopers#getClassesFullPath()
     */
    @Override
    public List<String> getClassesFullPath() {
        List<String> returnedClasses = new ArrayList<String>();
        returnedClasses.add("com.bitdubai.fermat_p2p_plugin.layer.communication.cloud_server.developer.bitdubai.version_1.WsCommunicationsServerCloudPluginRoot");
        returnedClasses.add("com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.ECCKeyPair");
        /**
         * I return the values.
         */
        return returnedClasses;
    }

    /**
     * (non-Javadoc)
     *
     * @see LogManagerForDevelopers#setLoggingLevelPerClass(Map<String, LogLevel>)
     */
    @Override
    public void setLoggingLevelPerClass(Map<String, LogLevel> newLoggingLevel) {
        /**
         * I will check the current values and update the LogLevel in those which is different
         */

        for (Map.Entry<String, LogLevel> pluginPair : newLoggingLevel.entrySet()) {
            /**
             * if this path already exists in the Root.bewLoggingLevel I'll update the value, else, I will put as new
             */
            if (WsCommunicationsServerCloudPluginRootOld.newLoggingLevel.containsKey(pluginPair.getKey())) {
                WsCommunicationsServerCloudPluginRootOld.newLoggingLevel.remove(pluginPair.getKey());
                WsCommunicationsServerCloudPluginRootOld.newLoggingLevel.put(pluginPair.getKey(), pluginPair.getValue());
            } else {
                WsCommunicationsServerCloudPluginRootOld.newLoggingLevel.put(pluginPair.getKey(), pluginPair.getValue());
            }
        }
    }

    /**
     * Static method to get the logging level from any class under root.
     * @param className
     * @return
     */
    public static LogLevel getLogLevelByClass(String className){
        try{
            /**
             * sometimes the classname may be passed dinamically with an $moretext
             * I need to ignore whats after this.
             */
            String[] correctedClass = className.split((Pattern.quote("$")));
            return WsCommunicationsServerCloudPluginRootOld.newLoggingLevel.get(correctedClass[0]);
        } catch (Exception e){
            /**
             * If I couldn't get the correct loggin level, then I will set it to minimal.
             */
            return DEFAULT_LOG_LEVEL;
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see DealsWithPluginFileSystem#setPluginFileSystem(PluginFileSystem)
     */
    @Override
    public void setPluginFileSystem(PluginFileSystem pluginFileSystem) {
    	//this.pluginFileSystem = pluginFileSystem;
    }

    /**
     * (non-Javadoc)
     *
     * @see DealsWithEvents#setEventManager(EventManager)
     */
    @Override
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * (non-Javadoc)
     *
     * @see DealsWithErrors#setErrorManager(ErrorManager)
     */
    @Override
    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    /**
     * (non-Javadoc)
     *
     * @see Plugin#setId(UUID)
     */
    @Override
    public void setId(UUID pluginId) {
       this.pluginId = pluginId;
    }

    /**
     * Get the disable server flag
     *
     * @return Boolean
     */
    public Boolean getDisableServerFlag() {
        return disableServerFlag;
    }

    /**
     * Set Disable Server Flag
     *
     * @param disableServerFlag
     */
    public void setDisableServerFlag(Boolean disableServerFlag) {
        this.disableServerFlag = disableServerFlag;
    }
}
