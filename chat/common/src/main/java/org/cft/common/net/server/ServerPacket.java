package org.cft.common.net.server;

import org.cft.common.net.ServerPacketVisitor;

public interface ServerPacket {
    void accept(ServerPacketVisitor visitor);
}