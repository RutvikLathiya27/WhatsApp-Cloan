package com.example.whatsappclone.managers;

import com.example.whatsappclone.model.chat.Chats;

import java.util.List;

public interface OnReadChatCallBack {
    void onReadSuccess(List<Chats> list);
    void onReadFailed();
}
