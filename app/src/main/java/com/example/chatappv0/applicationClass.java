package com.example.chatappv0;

import android.app.Application;

public class applicationClass extends Application {
    String name;
    private void loadString(String name){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
