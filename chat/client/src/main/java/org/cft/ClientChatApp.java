package org.cft;

import org.cft.controller.ChatController;
import org.cft.model.Chat;
import org.cft.view.ChatView;
import org.cft.view.SwingChatView;

public class ClientChatApp {
    public static void main(String[] args) {
        Chat chat = new Chat();
        ChatController chatController = new ChatController(chat);
        ChatView swingChatView = new SwingChatView(chatController);
        chat.attachView(swingChatView);
        chat.start();
    }
}
