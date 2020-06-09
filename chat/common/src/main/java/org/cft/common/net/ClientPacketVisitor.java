package org.cft.common.net;

import org.cft.common.net.client.ClientMessagePacket;
import org.cft.common.net.client.PongPacket;
import org.cft.common.net.client.ShutdownPacket;
import org.cft.common.net.client.SubmitNamePacket;

/**
 * Описывает методы для обработки пакетов от клиента из {@link org.cft.common.net.client}.
 */
public interface ClientPacketVisitor {
    void visit(ClientMessagePacket packet);
    void visit(PongPacket packet);
    void visit(ShutdownPacket packet);
    void visit(SubmitNamePacket packet);
}
