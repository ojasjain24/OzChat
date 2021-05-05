package com.example.chatappv0.Models;

public class meetingModel {
    String endTime, startTime, hostUid, key;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getHostUid() {
        return hostUid;
    }

    public void setHostUid(String hostUid) {
        this.hostUid = hostUid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public meetingModel(String endTime, String startTime, String hostUid, String key) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.hostUid = hostUid;
        this.key = key;
    }
    public meetingModel(){}
}
