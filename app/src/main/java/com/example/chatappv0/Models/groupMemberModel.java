package com.example.chatappv0.Models;

public class groupMemberModel {
    public String uid;
    public String status;
    public groupMemberModel(){

    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public groupMemberModel(String uid, String status) {
        this.uid = uid;
        this.status = status;
    }
}
