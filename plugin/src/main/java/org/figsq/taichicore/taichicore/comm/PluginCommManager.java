package org.figsq.taichicore.taichicore.comm;

import com.google.common.collect.Sets;
import lombok.val;
import me.fullidle.ficore.ficore.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.comm.handlers.CustomPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.server.CustomPacket;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PluginCommManager extends CommManager<Player> implements PluginMessageListener {
    public static final PluginCommManager INSTANCE = new PluginCommManager();
    private static final Map<Player, List<byte[]>> waitSend = new HashMap<>();
    private static final BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(TaiChiCore.getInstance(), ()->{
        synchronized (waitSend) {
            if (waitSend.isEmpty()) return;
            for (Player player : Sets.newHashSet(waitSend.keySet())) {
                if (!player.isOnline()) waitSend.remove(player);
                if (!INSTANCE.canSend(player)) continue;
                for (byte[] bytes : waitSend.remove(player))
                    player.sendPluginMessage(TaiChiCore.getInstance(), CHANNEL, bytes);
            }
        }
    }, 5L, 5L);

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
        if (canSend(target)) {
            target.sendPluginMessage(TaiChiCore.getInstance(), CHANNEL, bytes);
            return;
        }
        synchronized (waitSend) {
            waitSend.computeIfAbsent(target, k->new ArrayList<>()).add(bytes);
        }
    }

    public boolean canSend(Player target) {
        return target.getListeningPluginChannels().contains(CHANNEL);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, @NotNull byte[] bytes) {
        if (!CHANNEL.equals(s)) return;
        receive(player, bytes);
    }
}
