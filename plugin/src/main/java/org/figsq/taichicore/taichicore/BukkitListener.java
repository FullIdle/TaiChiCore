package org.figsq.taichicore.taichicore;

import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.figsq.taichicore.taichicore.events.CustomPacketEvent;

public class BukkitListener implements Listener {
    public static final BukkitListener INSTANCE = new BukkitListener();

    public static void register() {
        val plugin = TaiChiCore.getInstance();
        plugin.getServer().getPluginManager().registerEvents(INSTANCE, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        val player = event.getPlayer();
        TaiChiCoreAPI.INSTANCE.updateGuiConfig(player);
    }

    //最后执行
    @EventHandler(priority = EventPriority.HIGHEST/*, ignoreCancelled = true*/)
    public void customPacket(CustomPacketEvent event) {
        if (event.isCancelled()) return;
        if (TaiChiCoreAPI.INSTANCE.handleCustomPacket(event.getPacket(), event.getPlayer())) event.setCancelled(true);
    }
}
