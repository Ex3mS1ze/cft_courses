package org.cft.net;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cft.common.dto.Participant;
import org.cft.common.dto.TextMessage;
import org.cft.common.net.ServerPacketVisitor;
import org.cft.common.net.client.PongPacket;
import org.cft.common.net.server.*;
import org.cft.model.Chat;

import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
public class ServerPacketVisitorImpl implements ServerPacketVisitor {
    private final Chat chat;

    @Override
    public void visit(MessagePacket packet) {
        TextMessage message = packet.getMessage();
        boolean authored = message.getAuthor().getName().equals(chat.getAuthor().getName());
        chat.getViewNotifier().notifyViewAboutNewMessage(message, authored);
        log.info("Новое сообщение от участника {}", message.getAuthor().getName());
    }

    @Override
    public void visit(NameStatusPacket packet) {
        if (packet.getName() != null) {
            log.info("Сервер ответил, что имя доступно");
            chat.getAuthor().setName(packet.getName());
        } else {
            log.info("Сервер ответил, что имя занято");
            chat.getViewNotifier().notifyAboutNameUnavailable();
            chat.getViewNotifier().fetchUsername();
        }
    }

    @Override
    public void visit(ParticipantsPacket packet) {
        Participant[] participants = packet.getParticipants();
        String[] participantNames = Stream.of(participants).map(Participant::getName).toArray(String[]::new);
        chat.getViewNotifier().notifyAboutParticipantListChanged(participantNames);
        log.info("Список участников обновлен");
    }

    @Override
    public void visit(ParticipantStatusPacket packet) {
        Participant connectedParticipant = packet.getParticipant();

        if (packet.isConnected()) {
            if (connectedParticipant.getName().equals(chat.getAuthor().getName())) {
                chat.getViewNotifier().notifyConnected();
            } else {
                chat.getViewNotifier().notifyAboutParticipantConnected(connectedParticipant.getName());
                log.info("Новый участник подключился {}", connectedParticipant.getName());
            }
        } else {
            chat.getViewNotifier().notifyAboutParticipantDisconnected(connectedParticipant.getName());
            log.info("Участник отключился {}", connectedParticipant.getName());
        }
    }

    @Override
    public void visit(PingPacket packet) {
        chat.getSocketHandler().sendPacket(new PongPacket());
    }
}
