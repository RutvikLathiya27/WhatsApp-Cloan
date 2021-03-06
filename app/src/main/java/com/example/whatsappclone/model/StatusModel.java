package com.example.whatsappclone.model;

public class StatusModel {

    private String id;
    private String userId;
    private String createdDate;
    private String imageUrl;
    private String textStatus;
    private String viewCount;

    public StatusModel() {
    }

    public StatusModel(String id, String userId, String createdDate, String imageUrl, String textStatus, String viewCount) {
        this.id = id;
        this.userId = userId;
        this.createdDate = createdDate;
        this.imageUrl = imageUrl;
        this.textStatus = textStatus;
        this.viewCount = viewCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTextStatus() {
        return textStatus;
    }

    public void setTextStatus(String textStatus) {
        this.textStatus = textStatus;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }
}
