package com.nextlynxtech.gdspushnotification.services;

import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.nextlynxtech.gdspushnotification.MainApplication;
import com.nextlynxtech.gdspushnotification.classes.Message;
import com.nextlynxtech.gdspushnotification.classes.NewMessageCalls;
import com.nextlynxtech.gdspushnotification.classes.NewMessageResult;
import com.nextlynxtech.gdspushnotification.classes.SQLFunctions;

import de.greenrobot.event.EventBus;

public class MessageServices extends WakefulIntentService {
    public MessageServices() {
        super("MessageServices");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        try {
            NewMessageResult m = MainApplication.service.GetNewMessages(new NewMessageCalls("12", "1234"));
            Log.e("SIZE", m.getMessages().size() + " messages");
            if (m.getMessages().size() > 0) {
                SQLFunctions sql = new SQLFunctions(MessageServices.this);
                sql.open();
                for (Message message : m.getMessages()) {
                    sql.insertMessage(message, 0);
                }
                sql.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post("done");
    }
}
