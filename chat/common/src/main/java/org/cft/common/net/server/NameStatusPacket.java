package org.cft.common.net.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cft.common.net.ServerPacketVisitor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class NameStatusPacket implements ServerPacket, Serializable {
    private final String name;

    @Override
    public void accept(ServerPacketVisitor visitor) {
        visitor.visit(this);
    }
}
