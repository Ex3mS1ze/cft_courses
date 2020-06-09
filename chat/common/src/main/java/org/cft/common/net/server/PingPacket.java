package org.cft.common.net.server;

import org.cft.common.net.ServerPacketVisitor;

import java.io.Serializable;

public class PingPacket implements ServerPacket, Serializable {
    @Override
    public void accept(ServerPacketVisitor visitor) {
        visitor.visit(this);
    }
}
