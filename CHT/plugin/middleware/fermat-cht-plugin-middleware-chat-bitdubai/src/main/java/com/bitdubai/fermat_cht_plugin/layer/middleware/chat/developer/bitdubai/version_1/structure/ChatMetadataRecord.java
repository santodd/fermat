package com.bitdubai.fermat_cht_plugin.layer.middleware.chat.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.util.XMLParser;
import com.bitdubai.fermat_cht_api.layer.network_service.chat.enums.ChatMessageStatus;
import com.bitdubai.fermat_cht_api.layer.network_service.chat.enums.DistributionStatus;
import com.bitdubai.fermat_cht_api.layer.network_service.chat.interfaces.ChatMetadata;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 13/01/16.
 */
public class ChatMetadataRecord implements ChatMetadata {

    UUID chatId;
    UUID objectId;
    String localActorType;
    String localActorPublicKey;
    String remoteActorType;
    String remoteActorPublicKey;
    String chatName;
    ChatMessageStatus chatMessageStatus;
    Timestamp date;
    UUID messageId;
    String message;
    DistributionStatus distributionStatus;

    public ChatMetadataRecord(
            UUID chatId,
            UUID objectId,
            String localActorType,
            String localActorPublicKey,
            String remoteActorType,
            String remoteActorPublicKey,
            String chatName,
            ChatMessageStatus chatMessageStatus,
            Timestamp date,
            UUID messageId,
            String message,
            DistributionStatus distributionStatus) {
        this.chatId = chatId;
        this.objectId = objectId;
        this.localActorType = localActorType;
        this.localActorPublicKey = localActorPublicKey;
        this.remoteActorType = remoteActorType;
        this.remoteActorPublicKey = remoteActorPublicKey;
        this.chatName = chatName;
        this.chatMessageStatus = chatMessageStatus;
        this.date = date;
        this.messageId = messageId;
        this.message = message;
        this.distributionStatus = distributionStatus;
    }

    @Override
    public UUID getIdChat() {
        return this.chatId;
    }

    @Override
    public UUID getIdObject() {
        return this.objectId;
    }

    @Override
    public String getLocalActorType() {
        return this.localActorType;
    }

    @Override
    public String getLocalActorPubKey() {
        return this.localActorPublicKey;
    }

    @Override
    public String getRemoteActorType() {
        return this.remoteActorType;
    }

    @Override
    public String getRemoteActorPubKey() {
        return this.remoteActorPublicKey;
    }

    @Override
    public String getChatName() {
        return this.chatName;
    }

    @Override
    public ChatMessageStatus getChatMessageStatus() {
        return this.chatMessageStatus;
    }

    @Override
    public Timestamp getDate() {
        return this.date;
    }

    @Override
    public UUID getIdMessage() {
        return this.messageId;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public DistributionStatus getDistributionStatus() {
        return this.distributionStatus;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public void setObjectId(UUID objectId) {
        this.objectId = objectId;
    }

    public void setLocalActorType(String localActorType) {
        this.localActorType = localActorType;
    }

    public void setLocalActorPublicKey(String localActorPublicKey) {
        this.localActorPublicKey = localActorPublicKey;
    }

    public void setRemoteActorType(String remoteActorType) {
        this.remoteActorType = remoteActorType;
    }

    public void setRemoteActorPublicKey(String remoteActorPublicKey) {
        this.remoteActorPublicKey = remoteActorPublicKey;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setChatMessageStatus(ChatMessageStatus chatMessageStatus) {
        this.chatMessageStatus = chatMessageStatus;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDistributionStatus(DistributionStatus distributionStatus) {
        this.distributionStatus = distributionStatus;
    }

    /**
     * This method returns a XML String with all the objects set in this record
     * @return
     */
    public String toString(){
        return XMLParser.parseObject(this);
    }
}
