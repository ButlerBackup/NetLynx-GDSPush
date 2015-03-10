package com.nextlynxtech.gdspushnotification.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Probook2 on 30/12/2014.
 */
public class WebAPIOutput {
    @SerializedName("StatusCode")
    private int statusCode;

    @SerializedName("StatusDescription")
    private String statusDescription;

    @SerializedName("DeleteAllFlag")
    private String deleteAllFlag;

    @SerializedName("UserName")
    private String verifyPinUsername;

    @SerializedName("UserGroup")
    private String verifyPinUserGroup;

    @SerializedName("CreateMessage")
    private int createMessage;

    @SerializedName("PhotoUpload")
    private int photoUpload;

    @SerializedName("ReceiveMessage")
    private int receiveMessage;

    public int getCreateMessage() {
        return createMessage;
    }

    public void setCreateMessage(int createMessage) {
        this.createMessage = createMessage;
    }

    public int getPhotoUpload() {
        return photoUpload;
    }

    public void setPhotoUpload(int photoUpload) {
        this.photoUpload = photoUpload;
    }

    public int getReceiveMessage() {
        return receiveMessage;
    }

    public void setReceiveMessage(int receiveMessage) {
        this.receiveMessage = receiveMessage;
    }

    public String getVerifyPinUsername() {
        return verifyPinUsername;
    }

    public void setVerifyPinUsername(String verifyPinUsername) {
        this.verifyPinUsername = verifyPinUsername;
    }

    public String getVerifyPinUserGroup() {
        return verifyPinUserGroup;
    }

    public void setVerifyPinUserGroup(String verifyPinUserGroup) {
        this.verifyPinUserGroup = verifyPinUserGroup;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getDeleteAllFlag() {
        return deleteAllFlag;
    }

    public void setDeleteAllFlag(String deleteAllFlag) {
        this.deleteAllFlag = deleteAllFlag;
    }
}
