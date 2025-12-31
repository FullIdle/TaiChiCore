package org.figsq.taichicore.taichicore.events;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.figsq.taichicore.taichicore.common.comm.packets.server.CustomPacket;
import org.jetbrains.annotations.NotNull;

/**
 * 当客户端发送CustomPacket到服务器时触发该事件
 */
@Getter
public class CustomPacketEvent extends PlayerEvent {
    private final CustomPacket packet;

    public CustomPacketEvent(CustomPacket packet, Player sender) {
        super(sender);
        this.packet = packet;
    }

    @Getter
    public static final HandlerList handlerList = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }
}
