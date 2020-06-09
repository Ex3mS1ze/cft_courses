package org.cft.common.net.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cft.common.dto.Participant;
import org.cft.common.net.ServerPacketVisitor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class ParticipantsPacket implements ServerPacket, Serializable {
    private final Participant[] participants;

    @Override
    public void accept(ServerPacketVisitor visitor) {
        visitor.visit(this);
    }
}
