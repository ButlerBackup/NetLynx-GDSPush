package com.nextlynxtech.gdspushnotification.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Probook2 on 26/2/2015.
 */
public class MessageReply {

    @SerializedName("sessionID")
    String sessionId;

    @SerializedName("UDID")
    String udid;

    @SerializedName("messageID")
    int messageId;

    @SerializedName("reply")
    String reply;

    public MessageReply(String sessionId, String udid, int messageId, String reply) {
        this.sessionId = sessionId;
        this.udid = udid;
        this.messageId = messageId;
        this.reply = reply;
    }
}
