package com.example.chatappv0.Models;

import java.io.Serializable;

public class groupChatModel implements Serializable {

    public groupChatModel(String senderUid, String isThisFile, String key, String message, String time, String type) {
        this.senderUid=senderUid;
        this.isThisFile=isThisFile;
        this.key=key;
        this.message=message;
        this.time=time;
        this.type=type;
    }

    public String getIsThisFile() {
        return isThisFile;
    }

    public void setIsThisFile(String isThisFile) {
        this.isThisFile = isThisFile;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public groupChatModel() {

    }
    private String senderUid;
    private String message;
    private String isThisFile;
    private String time;
    private String type;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
