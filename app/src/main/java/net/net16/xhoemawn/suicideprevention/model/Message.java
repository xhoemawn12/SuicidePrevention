package net.net16.xhoemawn.suicideprevention.model;

import java.io.Serializable;

/**
 * Created by xhoemawn12 on 5/1/17.
 */

public class Message implements Serializable {

    private String messageBody;
    private String imageURI;

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    private String chatId;
    private String senderId;
    private long timeStamp;
    public Message(){

    }

}

