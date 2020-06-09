package org.cft.common.net.client;

import org.cft.common.net.ClientPacketVisitor;

import java.io.Serializable;

public class PongPacket implements Serializable, ClientPacket {
    @Override
    public void accept(ClientPacketVisitor visitor) {
        visitor.visit(this);
    }
}
