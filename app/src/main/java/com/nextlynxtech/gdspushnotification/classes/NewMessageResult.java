package com.nextlynxtech.gdspushnotification.classes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Probook2 on 25/2/2015.
 */
public class NewMessageResult {
    @SerializedName("StatusCode")
    private String status;

    @SerializedName("StatusDescription")
    private String statusDescription;

    @SerializedName("ErrorCode")
    private String errorCode;

    @SerializedName("MessageCount")
    private String messageCount;

    @SerializedName("lMessageDetails")
    private ArrayList<Message> messages;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(String messageCount) {
        this.messageCount = messageCount;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
