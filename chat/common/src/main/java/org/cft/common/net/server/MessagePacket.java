package org.cft.common.net.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cft.common.dto.TextMessage;
import org.cft.common.net.ServerPacketVisitor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class MessagePacket implements ServerPacket, Serializable {
    private final TextMessage message;

    @Override
    public void accept(ServerPacketVisitor visitor) {
        visitor.visit(this);
    }
}
