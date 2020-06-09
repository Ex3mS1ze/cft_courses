package org.cft.model;

import org.cft.common.dto.TextMessage;
import org.cft.view.ChatView;

import java.util.ArrayList;
import java.util.List;

public class ChatViewNotifier {
    private final List<ChatView> chatViews = new ArrayList<>();

    void attachView(ChatView chatView) {
        chatViews.add(chatView);
    }

    void detachView(ChatView chatView) {
        chatViews.remove(chatView);
    }

    public void notifyViewAboutNewMessage(TextMessage message, boolean authored) {
        chatViews.forEach(chatView -> chatView.renderNewMessage(message, authored));
    }

    public void notifyAboutParticipantConnected(String participantName) {
        chatViews.forEach(chatView -> chatView.renderParticipantConnected(participantName));
    }

    public void notifyAboutParticipantDisconnected(String participantName) {
        chatViews.forEach(chatView -> chatView.renderParticipantDisconnected(participantName));
    }

    public void notifyAboutParticipantListChanged(String[] participantNames) {
        chatViews.forEach(chatView -> chatView.renderParticipants(participantNames));
    }

    void fetchConnectionSettings() {
        chatViews.forEach(ChatView::renderConnectionSettings);
    }

    public void fetchUsername() {
        chatViews.forEach(ChatView::renderNameRequest);
    }

    public void notifyConnected() {
        chatViews.forEach(ChatView::renderConnected);
    }

    void notifyDisconnected() {
        chatViews.forEach(ChatView::renderDisconnected);
    }

    public void notifyAboutNameUnavailable() {
        chatViews.forEach(ChatView::renderNameUnavailable);
    }
}
