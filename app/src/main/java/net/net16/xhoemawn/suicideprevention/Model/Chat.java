package net.net16.xhoemawn.suicideprevention.Model;

/**
 * Created by xhoemawn on 4/8/2017.
 */

public class Chat {
    private String cid;
    private String uid;
    private String uid_receiver;
    private String message;

    public Chat(){

    }
    public Chat(String cid, String uid, String uid_receiver, String message, String timeStamp) {
        this.cid = cid;
        this.uid = uid;
        this.uid_receiver = uid_receiver;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    private String timeStamp;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid_receiver() {
        return uid_receiver;
    }

    public void setUid_receiver(String uid_receiver) {
        this.uid_receiver = uid_receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
