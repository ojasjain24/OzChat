package com.affixchat.chatappv0.Models;

public class friendsModel {
    String uid;
    String lastmsg;
    public friendsModel(){

    }
    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }

    public friendsModel(String uid, String lastmsg) {
        this.uid = uid;
        this.lastmsg = lastmsg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public friendsModel(String uid) {
        this.uid = uid;
    }

}
