package com.nextlynxtech.gdspushnotification.api;

import com.nextlynxtech.gdspushnotification.classes.RegisterUser;
import com.nextlynxtech.gdspushnotification.classes.SubmitMessage;
import com.nextlynxtech.gdspushnotification.classes.VerifyPin;
import com.nextlynxtech.gdspushnotification.classes.WebAPIOutput;

import retrofit.http.Body;
import retrofit.http.POST;

public interface ApiService {

    @POST("/SubmitMessage")
    public WebAPIOutput uploadContentWithMessage(@Body SubmitMessage body);

    @POST("/Register")
    public WebAPIOutput registerUser(@Body RegisterUser body);

    @POST("/VerifyPIN")
    public WebAPIOutput verifyPin(@Body VerifyPin body);
}
