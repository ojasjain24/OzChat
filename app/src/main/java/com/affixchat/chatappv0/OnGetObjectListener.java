package com.affixchat.chatappv0;

public interface OnGetObjectListener<T> {

    void onGetObject(T object);
    void onFail(Exception e);
}
