package org.figsq.taichicore.taichicore;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
}
