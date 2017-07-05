package net.net16.xhoemawn.suicideprevention.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by xhoemawn12 on 5/1/17.
 */

public class Post implements Serializable{

    public Post(){}
    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public HashMap<String, Boolean> getCommends() {
        return commends;
    }

    public void setCommends(HashMap<String, Boolean> commends) {
        this.commends = commends;
    }

    private String postName;
    private String timeStamp;
    private String postBody;
    private String imageURL;
    private String postedBy;
    private HashMap<String,Boolean> commends;


}
