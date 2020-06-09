package org.cft.common.net.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cft.common.net.ClientPacketVisitor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class SubmitNamePacket implements Serializable, ClientPacket {
    private final String name;

    @Override
    public void accept(ClientPacketVisitor visitor) {
        visitor.visit(this);
    }
}
