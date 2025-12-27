package org.figsq.taichicore.taichicore.common.comm;

public interface IPacketHandler<T extends IPacket, S> {
    void handle(T packet, S sender);
}
