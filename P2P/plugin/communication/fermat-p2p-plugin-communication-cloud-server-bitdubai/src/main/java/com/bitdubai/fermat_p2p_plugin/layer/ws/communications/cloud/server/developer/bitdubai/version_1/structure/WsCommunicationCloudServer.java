/*
 * @#CommunicationServer.java - 2015
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.PlatformComponentProfile;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.ECCKeyPair;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.contents.FermatPacketCommunicationFactory;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.contents.FermatPacketDecoder;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.contents.FermatPacketEncoder;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.contents.FermatPacket;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.FermatPacketType;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.JsonAttNamesConstants;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.server.CommunicationCloudServer;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.processors.FermatPacketProcessor;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.vpn.WsCommunicationVpnServerManagerAgent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communication.server.developer.bitdubai.version_1.structure.WsCommunicationCloudServer</code> is
 * the web socket server to manage the communication
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 01/09/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class WsCommunicationCloudServer extends WebSocketServer implements CommunicationCloudServer {

    /**
     * Represent the logger instance
     */
    private Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(WsCommunicationCloudServer.class));

    /**
     * Represent the default port
     */
    public static final int DEFAULT_PORT = 9090;

    /**
     * Represent the wsCommunicationVpnServerManagerAgent
     */
    private WsCommunicationVpnServerManagerAgent wsCommunicationVpnServerManagerAgent;

    /**
     * Holds the pending register clients connections cache
     */
    private Map<String, WebSocket> pendingRegisterClientConnectionsCache;

    /**
     * Holds the registered clients connections cache
     */
    private Map<String, WebSocket> registeredClientConnectionsCache;

    /**
     * Holds the identity of the server by client the client connection hash
     */
    private Map<Integer, ECCKeyPair> serverIdentityByClientCache;

    /**
     * Holds the identity of the client by his connection hash
     */
    private Map<Integer, String> clientIdentityByClientConnectionCache;

    /**
     * Holds the packet processors objects
     */
    private Map<FermatPacketType, List<FermatPacketProcessor>> packetProcessorsRegister;

    //TODO: THE REGISTERED COMPONENT WOULD BE HOLD IN DATA BASE AND NO IN MEMORY, FOR SEARCH IN FUTURE

    /**
     * Holds all the registered Communications Cloud Server by his client connection hash
     */
    private Map<Integer, PlatformComponentProfile> registeredCommunicationsCloudServerCache;

    /**
     * Holds all the registered Communications Cloud Client by his client connection hash
     */
    private Map<Integer, PlatformComponentProfile> registeredCommunicationsCloudClientCache;

    /**
     * Holds all the registered network services by his network service type
     */
    private Map<NetworkServiceType, List<PlatformComponentProfile>> registeredNetworkServicesCache;

    /**
     * Holds all other Platform Component Profile register by type
     */
    private Map<PlatformComponentType, List<PlatformComponentProfile>> registeredOtherPlatformComponentProfileCache;

    /**
     * Holds all pong message by connection
     */
    private Map<Integer, Boolean> pendingPongMessageByConnection;

    /**
     * Holds all Timer that clear references by client identity
     */
    private Map<String, Timer> timersByClientIdentity;

    /**
     * Holds all profile by client identity, to wait to reconnect
     */
    private Map<String, List<PlatformComponentProfile>> standByProfileByClientIdentity;

    /**
     * Constructor with parameter
     * @param address
     */
    public WsCommunicationCloudServer(InetSocketAddress address) {
        super(address);
        this.wsCommunicationVpnServerManagerAgent         = new WsCommunicationVpnServerManagerAgent(address.getHostString(), address.getPort());
        this.pendingRegisterClientConnectionsCache        = new ConcurrentHashMap<>();
        this.registeredClientConnectionsCache             = new ConcurrentHashMap<>();
        this.serverIdentityByClientCache                  = new ConcurrentHashMap<>();
        this.clientIdentityByClientConnectionCache        = new ConcurrentHashMap<>();
        this.packetProcessorsRegister                     = new ConcurrentHashMap<>();
        this.registeredCommunicationsCloudServerCache     = new ConcurrentHashMap<>();
        this.registeredCommunicationsCloudClientCache     = new ConcurrentHashMap<>();
        this.registeredNetworkServicesCache               = new ConcurrentHashMap<>();
        this.registeredOtherPlatformComponentProfileCache = new ConcurrentHashMap<>();
        this.pendingPongMessageByConnection               = new ConcurrentHashMap<>();
        this.timersByClientIdentity                       = new ConcurrentHashMap<>();
        this.standByProfileByClientIdentity               = new ConcurrentHashMap<>();
    }

    /**
     * Send ping message to the remote node, to verify is connection
     * alive
     */
    public void sendPingMessage(WebSocket conn){
        LOG.debug("Sending ping message to remote node (" + conn.getRemoteSocketAddress() + ")");
        FramedataImpl1 frame = new FramedataImpl1(Framedata.Opcode.PING);
        frame.setFin(true);
        conn.sendFrame(frame);
        pendingPongMessageByConnection.put(conn.hashCode(), Boolean.TRUE);
    }

    /**
     * Receive pong message from the remote node, to verify is connection
     * alive
     *
     * @param conn
     * @param f
     */
    @Override
    public void onWebsocketPong(WebSocket conn, Framedata f) {
        if (f.getOpcode() == Framedata.Opcode.PONG){
            LOG.debug(" Pong message receiveRemote from node (" + conn.getRemoteSocketAddress() + ") connection is alive");
            pendingPongMessageByConnection.remove(conn.hashCode());
        }
    }


    /**
     * (non-javadoc)
     * @see WebSocketServer#onOpen(WebSocket, ClientHandshake)
     */
    @Override
    public void onOpen(WebSocket clientConnection, ClientHandshake handshake) {

        LOG.info(" --------------------------------------------------------------------- ");
        LOG.info("Starting method onOpen");
        LOG.info("New Client: " + clientConnection.getRemoteSocketAddress().getAddress().getHostAddress() + " is connected!");
        //System.out.println(" WsCommunicationCloudServer - Handshake Resource Descriptor = " + handshake.getResourceDescriptor());
        LOG.info("temp-i = " + handshake.getFieldValue(JsonAttNamesConstants.HEADER_ATT_NAME_TI));

        /*
         * Validate is a handshake valid
         */
        if (handshake.getFieldValue(JsonAttNamesConstants.HEADER_ATT_NAME_TI)     != null &&
                handshake.getFieldValue(JsonAttNamesConstants.HEADER_ATT_NAME_TI) != ""){

            /*
             * Get the temporal identity of the CommunicationsClientConnection componet
             */
            JsonParser parser = new JsonParser();
            JsonObject temporalIdentity = parser.parse(handshake.getFieldValue(JsonAttNamesConstants.HEADER_ATT_NAME_TI)).getAsJsonObject();
            String temporalClientIdentity = temporalIdentity.get(JsonAttNamesConstants.NAME_IDENTITY).getAsString();

            /*
             * Create a new server identity to talk with this client
             */
            ECCKeyPair serverIdentity = new ECCKeyPair();

            //System.out.println(" WsCommunicationCloudServer - private key for this connection = "+serverIdentity.getPrivateKey());
            //System.out.println(" WsCommunicationCloudServer - public key for this connection = "+serverIdentity.getPublicKey());

            /*
             * Get json representation for the serverIdentity
             */
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(JsonAttNamesConstants.SERVER_IDENTITY, serverIdentity.getPublicKey());

            /*
             * Construct a fermat packet whit the server identity
             */
            FermatPacket fermatPacket = FermatPacketCommunicationFactory.constructFermatPacketEncryptedAndSinged(temporalClientIdentity,                   //Destination
                                                                                                                 serverIdentity.getPublicKey(),            //Sender
                                                                                                                 jsonObject.toString(),                    //Message Content
                                                                                                                 FermatPacketType.SERVER_HANDSHAKE_RESPOND,//Packet type
                                                                                                                 serverIdentity.getPrivateKey());          //Sender private key
            /*
             * Send the encode packet to the client
             */
            clientConnection.send(FermatPacketEncoder.encode(fermatPacket));

            /*
             * Add to the pending register client connection
             */
            pendingRegisterClientConnectionsCache.put(temporalClientIdentity, clientConnection);

            /*
             * Add to the cache the server identity for this client connection
             */
            serverIdentityByClientCache.put(clientConnection.hashCode(), serverIdentity);

            /*
             * Add to the cache of client identity for his client connection
             */
            clientIdentityByClientConnectionCache.put(clientConnection.hashCode(), temporalClientIdentity);

        }else {

            clientConnection.closeConnection(404, "DENIED, NOT VALID HANDSHAKE");
        }
    }

    /**
     * (non-javadoc)
     * @see WebSocketServer#onClose(WebSocket, int, String, boolean)
     */
    @Override
    public void onClose(WebSocket clientConnection, int code, String reason, boolean remote) {

        LOG.info(" --------------------------------------------------------------------- ");
        LOG.info("Starting method onClose");
        LOG.info(clientConnection.getRemoteSocketAddress() + " is disconnect! code = " + code + " reason = " + reason + " remote = " + remote);

        switch (code){

            case 1006:

                if (remote){

                    LOG.info("Waiting for client reconnect");
                    final WebSocket connection = clientConnection;
                    final String clientIdentity = clientIdentityByClientConnectionCache.get(connection.hashCode());
                    final String address = connection.getRemoteSocketAddress().toString();

                    if(clientIdentity != null && clientIdentity != ""){

                        putReferencesToStandBy(connection);

                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                                           @Override
                                           public void run() {
                                               LOG.info("client ("+address+") not reconnect, proceed to clean references");
                                               standByProfileByClientIdentity.remove(clientIdentity);
                                               LOG.info("standByProfileByClientIdentity = "+standByProfileByClientIdentity.size());
                                           }
                                       },
                                60000
                        );

                        timersByClientIdentity.put(clientIdentity, timer);
                    }else {
                        cleanReferences(clientConnection);
                    }

                }

                break;

            default:

                cleanReferences(clientConnection);
                break;

        }


    }

    /**
     * (non-javadoc)
     * @see WebSocketServer#onMessage(WebSocket, String)
     */
    @Override
    public void onMessage(WebSocket clientConnection, String fermatPacketEncode) {

        LOG.info(" --------------------------------------------------------------------- ");
        LOG.info("Starting method onMessage");
       // System.out.println(" WsCommunicationCloudServer - encode fermatPacket = " + fermatPacketEncode);
       // System.out.println(" WsCommunicationCloudServer - server identity for this connection = " + serverIdentityByClientCache.get(clientConnection.hashCode()).getPrivateKey());

        /*
         * Get the server identity for this client connection
         */
        ECCKeyPair serverIdentity = serverIdentityByClientCache.get(clientConnection.hashCode());

        /*
         * Decode the fermatPacketEncode into a fermatPacket
         */
        FermatPacket fermatPacketReceive = FermatPacketDecoder.decode(fermatPacketEncode, serverIdentity.getPrivateKey());

        //System.out.println(" WsCommunicationCloudServer - decode fermatPacket = " + fermatPacketReceive.toJson());
        LOG.info("fermatPacket.getFermatPacketType() = " + fermatPacketReceive.getFermatPacketType());


        //verify is packet supported
        if (packetProcessorsRegister.containsKey(fermatPacketReceive.getFermatPacketType())){


            /*
             * Call the processors for this packet
             */
            for (FermatPacketProcessor fermatPacketProcessor :packetProcessorsRegister.get(fermatPacketReceive.getFermatPacketType())) {

                /*
                 * Processor make his job
                 */
                fermatPacketProcessor.processingPackage(clientConnection, fermatPacketReceive, serverIdentity);
            }


        } else {
            LOG.info("Packet type " + fermatPacketReceive.getFermatPacketType() + "is not supported");
        }

    }

    /**
     * (non-javadoc)
     * @see WebSocketServer#onError(WebSocket, Exception)
     */
    @Override
    public void onError(WebSocket clientConnection, Exception ex) {

        LOG.info("--------------------------------------------------------------------- ");
        LOG.info("Starting method onError");
        ex.printStackTrace();

        cleanReferences(clientConnection);

        /*
         * Close the connection
         */
        clientConnection.closeConnection(505, "- ERROR :" + ex.getLocalizedMessage());
    }

    /**
     * This method register a FermatJettyPacketProcessor object with this
     * server
     */
    public void registerFermatPacketProcessor(FermatPacketProcessor fermatPacketProcessor) {

        /*
         * Set server reference
         */
        fermatPacketProcessor.setWsCommunicationCloudServer(this);

        //Validate if a previous list created
        if (packetProcessorsRegister.containsKey(fermatPacketProcessor.getFermatPacketType())){

            /*
             * Add to the existing list
             */
            packetProcessorsRegister.get(fermatPacketProcessor.getFermatPacketType()).add(fermatPacketProcessor);

        }else{

            /*
             * Create a new list and add the fermatPacketProcessor
             */
            List<FermatPacketProcessor> fermatPacketProcessorList = new ArrayList<>();
            fermatPacketProcessorList.add(fermatPacketProcessor);

            /*
             * Add to the packetProcessorsRegister
             */
            packetProcessorsRegister.put(fermatPacketProcessor.getFermatPacketType(), fermatPacketProcessorList);
        }

        LOG.info("packetProcessorsRegister = " + packetProcessorsRegister.size());

    }

    /**
     * Clean all reference from the connection
     */
    private void cleanReferences(WebSocket clientConnection){

       try {

           /*
             * Clean all the caches, remove data bind whit this connection
             */
           Integer clientConnectionHashCode  = clientConnection.hashCode();
           LOG.info("clientConnectionHashCode = " + clientConnectionHashCode);
           String clientIdentity = clientIdentityByClientConnectionCache.get(clientConnectionHashCode);
           pendingPongMessageByConnection.remove(clientConnectionHashCode);

           LOG.info("clientIdentity           = " + clientIdentity);

           if(clientIdentity != null && clientIdentity != ""){
               removeNetworkServiceRegisteredByClientIdentity(clientIdentity);
               removeOtherPlatformComponentRegisteredByClientIdentity(clientIdentity);
               pendingRegisterClientConnectionsCache.remove(clientIdentity);
               registeredClientConnectionsCache.remove(clientIdentity);
           }

           serverIdentityByClientCache.remove(clientConnectionHashCode);
           clientIdentityByClientConnectionCache.remove(clientConnectionHashCode);
           registeredCommunicationsCloudServerCache.remove(clientConnectionHashCode);
           registeredCommunicationsCloudClientCache.remove(clientConnectionHashCode);

       }catch (Exception e){
           e.printStackTrace();
       }

        LOG.info("pendingRegisterClientConnectionsCache.size()    = " + pendingRegisterClientConnectionsCache.size());
        LOG.info("registeredClientConnectionsCache.size()         = " + registeredClientConnectionsCache.size());
        LOG.info("serverIdentityByClientCache.size()              = " + serverIdentityByClientCache.size());
        LOG.info("clientIdentityByClientConnectionCache.size()    = " + clientIdentityByClientConnectionCache.size());
        LOG.info("registeredCommunicationsCloudServerCache.size() = " + registeredCommunicationsCloudServerCache.size());
        LOG.info("registeredCommunicationsCloudClientCache.size() = " + registeredCommunicationsCloudClientCache.size());
        LOG.info("registeredNetworkServicesCache.size()           = " + registeredNetworkServicesCache.size());
        for (NetworkServiceType networkServiceType: registeredNetworkServicesCache.keySet()) {
            LOG.info(networkServiceType + " = " + registeredNetworkServicesCache.get(networkServiceType).size());
        }
        LOG.info("registeredOtherPlatformComponentProfileCache.size()  = " + registeredOtherPlatformComponentProfileCache.size());
        for (PlatformComponentType platformComponentType: registeredOtherPlatformComponentProfileCache.keySet()) {
            LOG.info(platformComponentType + " = " + registeredOtherPlatformComponentProfileCache.get(platformComponentType).size());
        }
    }


    /**
     * Clean all reference from the registers cache into
     * a standByCache to wait to reconnect
     */
    private void putReferencesToStandBy(WebSocket clientConnection){

        try {

            LOG.info(" putReferencesToStandBy = " + clientConnection.getRemoteSocketAddress());

           /*
             * Clean all the caches, remove data bind whit this connection and put
             * on stand by, to wait to reconnect
             */
            Integer clientConnectionHashCode  = clientConnection.hashCode();
            String clientIdentity = clientIdentityByClientConnectionCache.remove(clientConnectionHashCode);

            List<PlatformComponentProfile> removeProfile = removeNetworkServiceRegisteredByClientIdentity(clientIdentity);
            removeProfile.addAll(removeOtherPlatformComponentRegisteredByClientIdentity(clientIdentity));
            pendingRegisterClientConnectionsCache.remove(clientIdentity);

            LOG.info("number of profile put into standby = " + removeProfile.size());
            standByProfileByClientIdentity.put(clientIdentity, removeProfile);

            serverIdentityByClientCache.remove(clientConnectionHashCode);
            registeredClientConnectionsCache.remove(clientIdentity);
            clientIdentityByClientConnectionCache.remove(clientConnectionHashCode);
            pendingPongMessageByConnection.remove(clientConnectionHashCode);
            registeredCommunicationsCloudServerCache.remove(clientConnectionHashCode);
            registeredCommunicationsCloudClientCache.remove(clientConnectionHashCode);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * This method unregister network service component profile
     * register
     */
    private List<PlatformComponentProfile> removeNetworkServiceRegisteredByClientIdentity(final String clientIdentity){

        LOG.info("removeNetworkServiceRegisteredByClientIdentity ");

        List<PlatformComponentProfile> removeProfile = new ArrayList<>();

        Iterator<NetworkServiceType> iteratorNetworkServiceType = registeredNetworkServicesCache.keySet().iterator();

        while (iteratorNetworkServiceType.hasNext()){

            NetworkServiceType networkServiceType = iteratorNetworkServiceType.next();
            Iterator<PlatformComponentProfile> iterator = registeredNetworkServicesCache.get(networkServiceType).iterator();
            while (iterator.hasNext()){

                /*
                 * Remove the platformComponentProfileRegistered
                 */
                PlatformComponentProfile platformComponentProfileRegistered = iterator.next();

                if(platformComponentProfileRegistered.getCommunicationCloudClientIdentity().equals(clientIdentity)){
                    LOG.info("removing =" + platformComponentProfileRegistered.getName());
                    removeProfile.add(platformComponentProfileRegistered);
                    iterator.remove();
                }
            }

            if (registeredNetworkServicesCache.get(networkServiceType).isEmpty()){
                registeredNetworkServicesCache.remove(networkServiceType);
            }
        }

        /*
         * Remove the networkServiceType empty
         */
        for (NetworkServiceType networkServiceType : registeredNetworkServicesCache.keySet()) {

            if (registeredNetworkServicesCache.get(networkServiceType).isEmpty()){
                registeredNetworkServicesCache.remove(networkServiceType);
            }
        }

        return removeProfile;

    }


    /**
     * This method unregister all platform component profile
     * register
     */
    private List<PlatformComponentProfile> removeOtherPlatformComponentRegisteredByClientIdentity(final String clientIdentity){

        LOG.info("removeOtherPlatformComponentRegisteredByClientIdentity ");

        List<PlatformComponentProfile> removeProfile = new ArrayList<>();
        Iterator<PlatformComponentType> iteratorPlatformComponentType = registeredOtherPlatformComponentProfileCache.keySet().iterator();
        while (iteratorPlatformComponentType.hasNext()){

            PlatformComponentType platformComponentType = iteratorPlatformComponentType.next();
            Iterator<PlatformComponentProfile> iterator = registeredOtherPlatformComponentProfileCache.get(platformComponentType).iterator();
            while (iterator.hasNext()){

                /*
                 * Remove the platformComponentProfileRegistered
                 */
                PlatformComponentProfile platformComponentProfileRegistered = iterator.next();
                if(platformComponentProfileRegistered.getCommunicationCloudClientIdentity().equals(clientIdentity)){
                    LOG.info("removing Other ="+platformComponentProfileRegistered.getName());
                    removeProfile.add(platformComponentProfileRegistered);
                    iterator.remove();
                }
            }

            /*
             * Remove the platformComponentType empty
             */
            if (registeredOtherPlatformComponentProfileCache.get(platformComponentType).isEmpty()){
                registeredOtherPlatformComponentProfileCache.remove(platformComponentType);
            }
        }

        return removeProfile;

    }

    /**
     * Get the wsCommunicationVpnServerManagerAgent
     * @return WsCommunicationVpnServerManagerAgent
     */
    public WsCommunicationVpnServerManagerAgent getWsCommunicationVpnServerManagerAgent() {
        return wsCommunicationVpnServerManagerAgent;
    }

    /**
     * Get the ClientIdentityByClientConnectionCache
     *
     * @return Map<Integer, String>
     */
    public Map<Integer, String> getClientIdentityByClientConnectionCache() {
        return clientIdentityByClientConnectionCache;
    }

    /**
     * Get the PendingRegisterClientConnectionsCache
     *
     * @return Map<String, WebSocket>
     */
    public Map<String, WebSocket> getPendingRegisterClientConnectionsCache() {
        return pendingRegisterClientConnectionsCache;
    }

    /**
     * Get the RegisteredClientConnectionsCache
     *
     * @return Map<String, WebSocket>
     */
    public Map<String, WebSocket> getRegisteredClientConnectionsCache() {
        return registeredClientConnectionsCache;
    }

    /**
     * Get the ServerIdentityByClientCache
     * @return Map<Integer, ECCKeyPair>
     */
    public Map<Integer, ECCKeyPair> getServerIdentityByClientCache() {
        return serverIdentityByClientCache;
    }

    /**
     * Get the RegisteredPlatformComponentProfileCache
     * @return Map<PlatformComponentType, List<PlatformComponentProfile>>
     */
    public Map<PlatformComponentType, List<PlatformComponentProfile>> getRegisteredOtherPlatformComponentProfileCache() {
        return registeredOtherPlatformComponentProfileCache;
    }

    /**
     * Get the RegisteredCommunicationsCloudServerCache
     * @return Map<Integer, PlatformComponentProfile>
     */
    public Map<Integer, PlatformComponentProfile> getRegisteredCommunicationsCloudServerCache() {
        return registeredCommunicationsCloudServerCache;
    }

    /**
     * Get the RegisteredCommunicationsCloudClientCache
     * @return Map<Integer, PlatformComponentProfile>
     */
    public Map<Integer, PlatformComponentProfile> getRegisteredCommunicationsCloudClientCache() {
        return registeredCommunicationsCloudClientCache;
    }

    /**
     * Get the RegisteredNetworkServicesCache
     * @return Map<NetworkServiceType, List<PlatformComponentProfile>>
     */
    public Map<NetworkServiceType, List<PlatformComponentProfile>> getRegisteredNetworkServicesCache() {
        return registeredNetworkServicesCache;
    }

    /**
     * Get the PacketProcessorsRegister
     * @return Map<FermatPacketType, List<FermatJettyPacketProcessor>>
     */
    public Map<FermatPacketType, List<FermatPacketProcessor>> getPacketProcessorsRegister() {
        return packetProcessorsRegister;
    }

    /**
     * Get the PendingPongMessageByConnection
     * @return Map<Integer, Boolean>
     */
    public Map<Integer, Boolean> getPendingPongMessageByConnection() {
        return pendingPongMessageByConnection;
    }

    /**
     * Gets the value of standByProfileByClientIdentity and returns
     *
     * @return standByProfileByClientIdentity
     */
    public Map<String, List<PlatformComponentProfile>> getStandByProfileByClientIdentity() {
        return standByProfileByClientIdentity;
    }

    /**
     * Gets the value of timersByClientIdentity and returns
     *
     * @return timersByClientIdentity
     */
    public Map<String, Timer> getTimersByClientIdentity() {
        return timersByClientIdentity;
    }
}
