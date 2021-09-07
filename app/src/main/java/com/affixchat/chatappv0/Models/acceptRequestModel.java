package com.affixchat.chatappv0.Models;

import java.io.Serializable;

public class acceptRequestModel implements Serializable {
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
