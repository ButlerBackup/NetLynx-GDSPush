package com.nextlynxtech.gdspushnotification.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Probook2 on 25/2/2015.
 */
public class NewMessageCalls {
    @SerializedName("UDID")
    String udid;
    @SerializedName("sessionID")
    String sessionID;

    public NewMessageCalls(String sessionId, String UDID) {
        this.sessionID = sessionId;
        this.udid = UDID;
    }

}
