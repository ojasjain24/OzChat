package com.example.chatappv0.Models;

public class acceptRequestModel {
    private String senderuid;
    private String receveruid;
    private String requestid;

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }


    public acceptRequestModel() {
    }

    public String getSenderuid() {
        return senderuid;
    }

    public void setSenderuid(String senderuid) {
        this.senderuid = senderuid;
    }

    public String getReceveruid() {
        return receveruid;
    }

    public void setReceveruid(String receveruid) {
        this.receveruid = receveruid;
    }

}
