package com.jpilay.bueesclient.models;

/**
 * Created by jpilay on 18/08/16.
 */
public class User {

    private String mUsername;
    private String mEmail;
    private String mGroup;

    public User(String mUsername, String mEmail, String mGroup) {
        this.mUsername = mUsername;
        this.mEmail = mEmail;
        this.mGroup = mGroup;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmGroup() {
        return mGroup;
    }

    public void setmGroup(String mGroup) {
        this.mGroup = mGroup;
    }
}
