package com.nextlynxtech.gdspushnotification.api;

import com.nextlynxtech.gdspushnotification.classes.GenericResult;
import com.nextlynxtech.gdspushnotification.classes.MessageReply;
import com.nextlynxtech.gdspushnotification.classes.NewMessageCalls;
import com.nextlynxtech.gdspushnotification.classes.NewMessageResult;
import com.nextlynxtech.gdspushnotification.classes.RegisterUser;
import com.nextlynxtech.gdspushnotification.classes.SubmitMessage;
import com.nextlynxtech.gdspushnotification.classes.UpdateMessageRead;
import com.nextlynxtech.gdspushnotification.classes.VerifyPin;
import com.nextlynxtech.gdspushnotification.classes.WebAPIOutput;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Probook2 on 25/2/2015.
 */
public interface APICalls {

    @POST("/GetNewMessages")
    public NewMessageResult GetNewMessages(@Body NewMessageCalls body);

    @POST("/UpdateMessageReply")
    public GenericResult UpdateMessageReply (@Body MessageReply body);
    @POST("/UpdateMessageReadStatus")
    public GenericResult UpdateMessageReadStatus (@Body UpdateMessageRead body);

    @POST("/SubmitMessage")
    public WebAPIOutput uploadContentWithMessage(@Body SubmitMessage body);

    @POST("/Register")
    public WebAPIOutput registerUser(@Body RegisterUser body);

    @POST("/VerifyPIN")
    public WebAPIOutput verifyPin(@Body VerifyPin body);
}
