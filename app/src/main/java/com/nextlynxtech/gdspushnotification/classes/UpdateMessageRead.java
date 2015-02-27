package com.nextlynxtech.gdspushnotification.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Probook2 on 26/2/2015.
 */
public class UpdateMessageRead {

    @SerializedName("sessionID")
    String sessionId;

    @SerializedName("UDID")
    String udid;

    @SerializedName("messageID")
    int messageId;

    public UpdateMessageRead(String sessionId, String udid, int messageId) {
        this.sessionId = sessionId;
        this.udid = udid;
        this.messageId = messageId;
    }
}
