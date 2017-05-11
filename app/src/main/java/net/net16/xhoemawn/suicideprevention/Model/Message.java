package net.net16.xhoemawn.suicideprevention.Model;

import android.net.Uri;

/**
 * Created by xhoemawn12 on 5/1/17.
 */

public class Message {

    private String messageBody;

    private Uri imageURI;

    public Uri getImageURI() {
        return imageURI;
    }

    public void setImageURI(Uri imageURL) {
        this.imageURI = imageURL;
    }

    private String chatId;
    private String senderId;
    private long timeStamp;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Message(){

    }



    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
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
}

