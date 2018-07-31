package com.lucsoninfotech.cmi.cmiworld.helper;

/**
 * Created by lucsonmacpc8 on 03/10/16.
 */
public class ChatMessageModel {

    private long id;
    private boolean isMe;
    private String message;
    private Long userId;
    private String dateTime;
    private String userName;
    private String sectionTitle;
    private int messageType;
    private String message_thumb;
    private String message_image;
    private boolean isHistory;
    private boolean isSend;
    private String message_video;
    private String message_video_thumb;
    private String message_audio;
    private boolean isReceive;

    public String getMessage_audio() {
        return message_audio;
    }

    public void setMessage_audio(String message_audio) {
        this.message_audio = message_audio;
    }

    public String getMessage_video() {
        return message_video;
    }

    public void setMessage_video(String message_video) {
        this.message_video = message_video;
    }

    public String getMessage_video_thumb() {
        return message_video_thumb;
    }

    public void setMessage_video_thumb(String message_video_thumb) {
        this.message_video_thumb = message_video_thumb;
    }

    public boolean isReceive() {
        return isReceive;
    }

    public void setReceive(boolean receive) {
        isReceive = receive;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public String getMessage_thumb() {
        return message_thumb;
    }

    public void setMessage_thumb(String message_thumb) {
        this.message_thumb = message_thumb;
    }

    public String getMessage_image() {
        return message_image;
    }

    public void setMessage_image(String message_image) {
        this.message_image = message_image;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
