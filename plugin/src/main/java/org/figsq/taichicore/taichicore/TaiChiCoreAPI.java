package org.figsq.taichicore.taichicore;

import lombok.val;
import org.bukkit.entity.Player;
import org.figsq.taichicore.taichicore.comm.PluginCommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.client.CleanUpGuiConfigPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.common.CustomPacket;
import org.figsq.taichicore.taichicore.common.comm.records.GuiConfig;
import org.figsq.taichicore.taichicore.common.comm.packets.client.UpdateGuiConfigPacket;
import org.figsq.taichicore.taichicore.config.CustomPacketHandlerManager;
import org.figsq.taichicore.taichicore.config.GuiConfigManager;

public class TaiChiCoreAPI {
    public static TaiChiCoreAPI INSTANCE = new TaiChiCoreAPI();

    public void updateGuiConfig(Player player) {
        val manager = PluginCommManager.INSTANCE;
        manager.sendTo(player, new CleanUpGuiConfigPacket());
        for (GuiConfig config : GuiConfigManager.configs) manager.sendTo(player, new UpdateGuiConfigPacket(config));
    }

    public boolean handleCustomPacket(CustomPacket packet, Player player) {
        return CustomPacketHandlerManager.handle(packet, player);
    }
}
