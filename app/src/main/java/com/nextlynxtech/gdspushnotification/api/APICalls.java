package com.nextlynxtech.gdspushnotification.api;

import com.nextlynxtech.gdspushnotification.classes.GenericResult;
import com.nextlynxtech.gdspushnotification.classes.MessageReply;
import com.nextlynxtech.gdspushnotification.classes.NewMessageCalls;
import com.nextlynxtech.gdspushnotification.classes.NewMessageResult;

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
    public GenericResult UpdateMessageReadStatus (@Body MessageReply body);
}
