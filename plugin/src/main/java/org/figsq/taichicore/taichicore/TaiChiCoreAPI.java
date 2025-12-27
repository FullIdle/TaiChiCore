package org.figsq.taichicore.taichicore;

import com.google.gson.JsonArray;
import lombok.val;
import org.bukkit.entity.Player;
import org.figsq.taichicore.taichicore.comm.PluginCommManager;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;
import org.figsq.taichicore.taichicore.common.comm.packets.client.UpdateConfigPacket;
import org.figsq.taichicore.taichicore.config.GuiConfigManager;

public class TaiChiCoreAPI {
    public static TaiChiCoreAPI INSTANCE = new TaiChiCoreAPI();

    public void sendToUpdateConfig(Player player) {
        val manager = PluginCommManager.INSTANCE;
        for (UpdateConfigPacket.UpdateType value : UpdateConfigPacket.UpdateType.values())
            manager.sendTo(player, createUpdatePacket(value));
    }

    public UpdateConfigPacket createUpdatePacket(UpdateConfigPacket.UpdateType type) {
        val packet = new UpdateConfigPacket();
        packet.type = type;
        switch (type) {
            case GUI: {
                val array = new JsonArray();
                for (GuiConfig config : GuiConfigManager.configs)
                    if (config.type.equals(GuiConfig.Type.OVERWRITE))
                        array.add(UpdateConfigPacket.GSON.toJsonTree(config));
                packet.jsonData = UpdateConfigPacket.GSON.toJson(array);
                return packet;
            }
        }

        throw new RuntimeException("Unsupported type");
    }
}
