package org.figsq.taichicore.taichicore.comm.handlers;

import org.bukkit.entity.Player;
import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.packets.common.CustomPacket;
import org.figsq.taichicore.taichicore.events.CustomPacketEvent;

public class CustomPacketHandler implements IPacketHandler<CustomPacket, Player> {
    public static final CustomPacketHandler INSTANCE = new CustomPacketHandler();

    @Override
    public void handle(CustomPacket packet, Player sender) {
        new CustomPacketEvent(packet, sender).call();
    }
}
