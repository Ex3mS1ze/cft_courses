package org.cft.common.net;

import org.cft.common.net.server.*;

/**
 * Описывает методы для обработки пакетов от сервера из {@link org.cft.common.net.server}.
 */
public interface ServerPacketVisitor {
    void visit(MessagePacket packet);
    void visit(NameStatusPacket packet);
    void visit(ParticipantsPacket packet);
    void visit(ParticipantStatusPacket packet);
    void visit(PingPacket packet);
}
