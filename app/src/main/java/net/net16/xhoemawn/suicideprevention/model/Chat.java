package net.net16.xhoemawn.suicideprevention.model;

import java.util.HashMap;

/**
 * Created by xhoemawn on 4/8/2017.
 */

public class Chat {

    private String nameOfChat;
    private HashMap<String,Boolean> users;
    private long timeStamp;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public HashMap<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, Boolean> users) {
        this.users = users;
    }

    public String getNameOfChat() {
        return nameOfChat;
    }

    public void setNameOfChat(String nameOfChat) {
        this.nameOfChat = nameOfChat;
    }


    public Chat(String nameOfChat,HashMap hashMap) {
        this.users = hashMap;
        this.nameOfChat = nameOfChat;
    }
    public Chat(){

    }
}