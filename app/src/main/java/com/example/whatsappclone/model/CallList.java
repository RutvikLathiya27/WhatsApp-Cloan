package com.example.whatsappclone.model;

public class CallList {

    private String userId;
    private String userName;
    private String date;
    private String urlProfile;
    private String calltype;

    public CallList(){

    }

    public CallList(String userId, String userName, String date, String urlProfile, String calltype) {
        this.userId = userId;
        this.userName = userName;
        this.date = date;
        this.urlProfile = urlProfile;
        this.calltype = calltype;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }
}
