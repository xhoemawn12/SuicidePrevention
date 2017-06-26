package net.net16.xhoemawn.suicideprevention.model;

/**
 * Created by xhoemawn12 on 4/26/17.
 */

public class User {
    private Integer userType;
    private String name;
    private String email;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    private String imageURL;
    public Integer getUserType() {
        /*switch(userType){
            case 1: return UserType.ADMIN;
            case 2: return UserType.HELPER;
            case 3: return UserType.VICTIM;
        }*/
        return userType;
    }

    public void setUserType(Integer userTypeEnum) {
        this.userType = userTypeEnum;
    }

    private long lastLogin;
    private String password;
    private boolean available;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}