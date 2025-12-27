package org.figsq.taichicore.taichicore.comm;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.comm.handlers.CustomPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.server.CustomPacket;
import org.jetbrains.annotations.NotNull;

public class PluginCommManager extends CommManager<Player> implements PluginMessageListener {
    public static final PluginCommManager INSTANCE = new PluginCommManager();

    /**
     * 注册通讯
     */
    public void init() {
        super.init();

        val plugin = TaiChiCore.getInstance();
        val messenger = Bukkit.getMessenger();
        messenger.registerOutgoingPluginChannel(plugin, CHANNEL);
        messenger.registerIncomingPluginChannel(plugin, CHANNEL, this);
    }

    public void registerHandler() {
        registerHandler(CustomPacket.class, CustomPacketHandler.INSTANCE);
    }

    @Override
    public void sendTo(Player target, byte[] bytes) {
        target.sendPluginMessage(TaiChiCore.getInstance(), CHANNEL, bytes);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, @NotNull byte[] bytes) {
        if (!CHANNEL.equals(s)) return;
        receive(player, bytes);
    }
}
