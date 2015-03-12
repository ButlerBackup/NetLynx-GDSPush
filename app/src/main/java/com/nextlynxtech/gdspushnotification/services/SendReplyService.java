package com.nextlynxtech.gdspushnotification.services;

import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.nextlynxtech.gdspushnotification.MainApplication;
import com.nextlynxtech.gdspushnotification.classes.GenericResult;
import com.nextlynxtech.gdspushnotification.classes.MessageReply;
import com.nextlynxtech.gdspushnotification.classes.SQLFunctions;
import com.nextlynxtech.gdspushnotification.classes.Utils;

import de.greenrobot.event.EventBus;

public class SendReplyService extends WakefulIntentService {
    public SendReplyService() {
        super("SendReplyService");
    }

    GenericResult m = null;

    @Override
    protected void doWakefulWork(Intent intent) {
        try {
            int messageId = intent.getIntExtra("messageId", 0);
            String messageContent = intent.getStringExtra("messageContent");
            Log.e("Data", messageId + "|" + messageContent);
            m = MainApplication.service.UpdateMessageReply(new MessageReply("", new Utils(SendReplyService.this).getUnique(), messageId, messageContent));
            Log.e("Result", m.getStatusDescription() + "|" + m.getStatusCode());
            SQLFunctions sql = new SQLFunctions(SendReplyService.this);
            sql.open();
            if (m != null && m.getStatusDescription().equals("OK") && m.getStatusCode().equals("1")) {
                sql.updateReply(messageId, "1");
            } else {
                sql.updateReply(messageId, "0");
            }
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post("SendReplyService");
        stopSelf();
    }
}