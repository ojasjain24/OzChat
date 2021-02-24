package com.example.chatappv0.Models;

import java.io.Serializable;

public class chatModel implements Serializable {
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

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receverUid) {
        this.receiverUid = receverUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public chatModel() {

    }

    public chatModel(String senderUid, String receiverUid, String message, String isThisFile, String time, String type, String isseen, String key) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.isThisFile = isThisFile;
        this.time = time;
        this.type = type;
        this.isseen = isseen;
        this.key =key;
    }

    private String senderUid;
    private String receiverUid;
    private String message;
    private String isThisFile;
    private String time;
    private String type;
    private String isseen;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIsseen() {
        return isseen;
    }

    public void setIsseen(String isseen) {
        this.isseen = isseen;
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


