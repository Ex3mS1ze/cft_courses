package org.cft.model;

import org.cft.common.dto.Participant;
import org.cft.common.dto.TextMessage;
import org.cft.common.net.server.MessagePacket;
import org.cft.common.net.server.NameStatusPacket;
import org.cft.common.net.server.ParticipantStatusPacket;
import org.cft.common.net.server.ParticipantsPacket;
import org.cft.net.ChatSocketHandler;
import org.cft.view.ChatView;
import org.cft.view.SwingChatView;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
public class ChatTest {
    private ChatView chatViewMock;
    private Chat chatSpy;

    @Before
    public void setUp() {
        chatSpy = spy(new Chat());
        chatViewMock = mock(SwingChatView.class);
        chatSpy.attachView(chatViewMock);
    }

    @Test
    public void start_callRenderConnectionSettingsAndRenderNameRequest_whenCalled() {
        doNothing().when(chatViewMock).renderConnectionSettings();

        chatSpy.start();

        verify(chatViewMock, times(1)).renderConnectionSettings();
    }

    @Test
    public void setConnectionSettings_callRenderConnectionSettings_whenPassedInvalidHost() {
        doNothing().when(chatViewMock).renderConnectionSettings();

        chatSpy.setConnectionSettings("0.0.0.0", "6666");

        verify(chatViewMock, times(1)).renderConnectionSettings();
    }

    @Test
    public void setConnectionSettings_callRenderConnectionSettings_whenPassedInvalidPort() {
        doNothing().when(chatViewMock).renderConnectionSettings();

        chatSpy.setConnectionSettings("localhost", "66666");

        verify(chatViewMock, times(1)).renderConnectionSettings();
    }

    @Test
    public void handleReconnect_callRenderDisconnected_whenCalled() {
        doNothing().when(chatViewMock).renderDisconnected();
        ChatSocketHandler socketHandlerMock = mock(ChatSocketHandler.class);
        Whitebox.setInternalState(chatSpy, "socketHandler", socketHandlerMock);
        doNothing().when(chatSpy).exit();

        chatSpy.handleReconnect();

        verify(chatViewMock, times(1)).renderDisconnected();
    }

    @Test
    public void confirmName_callRenderNameRequest_whenPassInvalidName() {
        doNothing().when(chatViewMock).renderNameRequest();

        chatSpy.confirmName(" ");

        verify(chatViewMock, times(1)).renderNameRequest();
    }

    @Test(expected = NullPointerException.class)
    public void confirmName_notCallRenderNameRequest_whenPassValidName() {
        doNothing().when(chatViewMock).renderNameRequest();

        chatSpy.confirmName("Name");

        verify(chatViewMock, times(0)).renderNameRequest();
    }

    @Test
    public void sendMessage_notCallSendPacket_whenPassedBlankMessage() {
        Whitebox.setInternalState(chatSpy, "author", new Participant("name"));
        ChatSocketHandler socketHandlerMock = mock(ChatSocketHandler.class);
        doNothing().when(socketHandlerMock).sendPacket(any());
        Whitebox.setInternalState(chatSpy, "socketHandler", socketHandlerMock);

        chatSpy.sendMessage(" \n \r");

        verify(socketHandlerMock, never()).sendPacket(any());
    }

    @Test
    public void sendMessage_callSendPacket_whenPassedNonBlankMessage() {
        Whitebox.setInternalState(chatSpy, "author", new Participant("name"));
        ChatSocketHandler socketHandlerMock = mock(ChatSocketHandler.class);
        doNothing().when(socketHandlerMock).sendPacket(any());
        Whitebox.setInternalState(chatSpy, "socketHandler", socketHandlerMock);

        chatSpy.sendMessage("some message");

        verify(socketHandlerMock, times(1)).sendPacket(any());

    }

    @Test
    public void handlePacket_callRenderNewMessage_whenPassedServerMessagePacket() {
        TextMessage message = new TextMessage(new Participant("name"), "some text");
        doNothing().when(chatViewMock).renderNewMessage(message, false);

        chatSpy.handlePacket(new MessagePacket(message));

        verify(chatViewMock, times(1)).renderNewMessage(message, false);
    }

    @Test
    public void handlePacket_callRenderParticipantDisconnected_whenPassedParticipantStatusPacketConnected() {
        Participant participant = new Participant("name");
        doNothing().when(chatViewMock).renderParticipantConnected(participant.getName());

        chatSpy.handlePacket(new ParticipantStatusPacket(participant, true));

        verify(chatViewMock, times(1)).renderParticipantConnected(participant.getName());
    }

    @Test
    public void handlePacket_callRenderParticipantDisconnected_whenPassedParticipantStatusPacketDisconnected() {
        Participant participant = new Participant("name");
        doNothing().when(chatViewMock).renderParticipantDisconnected(participant.getName());

        chatSpy.handlePacket(new ParticipantStatusPacket(participant, false));

        verify(chatViewMock, times(1)).renderParticipantDisconnected(participant.getName());
    }

    @Test
    public void handlePacket_callRenderConnected_whenPassedParticipantStatusPacketConnectedAndAuthorEqParticipant() {
        Participant participant = new Participant("name");
        Whitebox.getInternalState(chatSpy, Participant.class).setName("name");
        doNothing().when(chatViewMock).renderConnected();

        chatSpy.handlePacket(new ParticipantStatusPacket(participant, true));

        verify(chatViewMock, times(1)).renderConnected();
    }

    @Test
    public void handlePacket_callRenderParticipants_whenPassedParticipantsPacket() {
        Participant[] participants = {new Participant("name"), new Participant("name1")};
        String[] participantNames = Stream.of(participants).map(Participant::getName).toArray(String[]::new);
        doNothing().when(chatViewMock).renderParticipants(participantNames);

        chatSpy.handlePacket(new ParticipantsPacket(participants));

        verify(chatViewMock, times(1)).renderParticipants(participantNames);
    }

    @Test
    public void handlePacket_callRenderNameRequest_whenPassedNameStatusPacketWithNull() {
        doNothing().when(chatViewMock).renderNameRequest();

        chatSpy.handlePacket(new NameStatusPacket(null));

        verify(chatViewMock, times(1)).renderNameRequest();
    }

    @Test
    public void handlePacket_callRenderNameRequest_whenPassedNameStatusPacketWithName() {
        String expectedName = "name";
        Participant actualName = Whitebox.getInternalState(chatSpy, "author");

        chatSpy.handlePacket(new NameStatusPacket(expectedName));

        assertEquals(expectedName, actualName.getName());
    }
}