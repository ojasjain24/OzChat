package com.example.chatappv0.Models;

public class groupDataModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupicon() {
        return groupicon;
    }

    public void setGroupicon(String groupicon) {
        this.groupicon = groupicon;
    }

    public groupDataModel(String name, String description, String groupicon,String nodeid) {
        this.name = name;
        this.description = description;
        this.groupicon = groupicon;
        this.nodeid=nodeid;
    }

    public groupDataModel(){

    }

    public String name;
    public String description;
    public String groupicon;
    public String nodeid;

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }
}
