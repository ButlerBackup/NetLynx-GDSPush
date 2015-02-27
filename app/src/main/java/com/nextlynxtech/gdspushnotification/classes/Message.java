package com.nextlynxtech.gdspushnotification.classes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Probook2 on 25/2/2015.
 */
public class Message implements Serializable {
    @SerializedName("Color")
    int color;

    @SerializedName("EventDate")
    String eventDate;

    @SerializedName("EventID")
    int eventId;

    @SerializedName("EventName")
    String eventName;

    @SerializedName("EventStatus")
    int eventStatus;

    @SerializedName("Message")
    String message;

    @SerializedName("MessageDate")
    String messageDate;

    @SerializedName("MessageHeader")
    String messageHeader;


    @SerializedName("MessageID")
    int messageId;

    @SerializedName("RecallFlag")
    int recallFlag;

    int read;
    int mine;

    @SerializedName("lReplyOptions")
    private ArrayList<String> replies;

    public ArrayList<String> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<String> replies) {
        this.replies = replies;
    }


    public int getMine() {
        return mine;
    }

    public void setMine(int mine) {
        this.mine = mine;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(int eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(String messageHeader) {
        this.messageHeader = messageHeader;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getRecallFlag() {
        return recallFlag;
    }

    public void setRecallFlag(int recallFlag) {
        this.recallFlag = recallFlag;
    }


}
