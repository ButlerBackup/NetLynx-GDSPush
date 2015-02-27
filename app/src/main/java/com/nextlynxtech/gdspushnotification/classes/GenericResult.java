package com.nextlynxtech.gdspushnotification.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Probook2 on 26/2/2015.
 */
public class GenericResult {
    @SerializedName("ErrorCode")
    int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @SerializedName("StatusCode")
    String statusCode;

    @SerializedName("StatusDescription")
    String statusDescription;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
