package org.cft.common.net.client;

import org.cft.common.net.ClientPacketVisitor;

public interface ClientPacket {
    void accept(ClientPacketVisitor visitor);
}