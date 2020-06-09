package org.cft.common.net.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cft.common.dto.TextMessage;
import org.cft.common.net.ClientPacketVisitor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class ClientMessagePacket implements Serializable, ClientPacket {
    private final TextMessage message;

    @Override
    public void accept(ClientPacketVisitor visitor) {
        visitor.visit(this);
    }
}
