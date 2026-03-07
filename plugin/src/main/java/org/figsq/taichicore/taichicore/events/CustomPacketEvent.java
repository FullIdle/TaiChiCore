package org.figsq.taichicore.taichicore.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.figsq.taichicore.taichicore.common.comm.packets.common.CustomPacket;
import org.jetbrains.annotations.NotNull;

/**
 * 当客户端发送CustomPacket到服务器时触发该事件
 */
@Getter
@Setter
public class CustomPacketEvent extends PlayerEvent implements Cancellable {
    private final CustomPacket packet;
    private boolean cancelled = false;

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
