package net.net16.xhoemawn.suicideprevention.Model;

/**
 * Created by xhoemawn12 on 4/26/17.
 */

public class User {
    public static final Integer VICTIM = 0;
    public static final Integer MODERATOR = 1;
    public static final Integer HELPER = 2;

    private String name;
    private String email;
    private long lastLogin;
    private String password;
    private boolean available;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    private Integer userType;
    public User(){

    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
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
