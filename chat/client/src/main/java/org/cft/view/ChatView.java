package org.cft.view;

import org.cft.common.dto.TextMessage;

public interface ChatView {
    void renderParticipants(String[] participantNames);

    void renderNameRequest();

    void renderNewMessage(TextMessage message, boolean authored);

    void renderParticipantConnected(String participantName);

    void renderParticipantDisconnected(String participantName);

    void renderConnectionSettings();

    void renderConnected();

    void renderDisconnected();

    void renderNameUnavailable();
}
