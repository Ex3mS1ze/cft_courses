package org.cft.controller;

import lombok.AllArgsConstructor;
import org.cft.model.Chat;

@AllArgsConstructor
public class ChatController {
    private final Chat chat;

    public void handleSendMessage(String message) {
        chat.sendMessage(message);
    }

    public void handleConfirmationUsername(String name) {
        chat.confirmName(name);
    }

    public void handleConnectionSettings(String host, String port) {
        chat.setConnectionSettings(host, port);
    }

    public void handleExit() {
        chat.exit();
    }
}
