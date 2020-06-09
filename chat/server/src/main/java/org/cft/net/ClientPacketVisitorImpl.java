package org.cft.net;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cft.common.net.ClientPacketVisitor;
import org.cft.common.net.client.ClientMessagePacket;
import org.cft.common.net.client.PongPacket;
import org.cft.common.net.client.ShutdownPacket;
import org.cft.common.net.client.SubmitNamePacket;
import org.cft.common.net.server.MessagePacket;

@Slf4j
@AllArgsConstructor
public class ClientPacketVisitorImpl implements ClientPacketVisitor {
    private final ClientHandler clientHandler;

    @Override
    public void visit(ClientMessagePacket packet) {
        if (!clientHandler.isAuthorFetched()) {
            log.warn("Участник пытается посылать запросы, но не подтвердил имя");
            clientHandler.setListening(false);
            return;
        }

        clientHandler.getServer().sendToAll(new MessagePacket(packet.getMessage()));
    }

    @Override
    public void visit(PongPacket packet) {
        log.trace("Клиент '{}' еще жив", clientHandler.getAuthor().getName());
    }

    @Override
    public void visit(ShutdownPacket packet) {
        clientHandler.setListening(false);
        log.info("Клиент отключился");
    }

    @Override
    public void visit(SubmitNamePacket packet) {
        clientHandler.fetchAuthor(packet.getName());
    }
}
