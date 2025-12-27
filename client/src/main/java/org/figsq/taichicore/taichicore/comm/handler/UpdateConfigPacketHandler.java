package org.figsq.taichicore.taichicore.comm.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.val;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;
import org.figsq.taichicore.taichicore.common.comm.packets.client.UpdateConfigPacket;

public class UpdateConfigPacketHandler implements IPacketHandler<UpdateConfigPacket, PacketSender> {
    public static final UpdateConfigPacketHandler INSTANCE = new UpdateConfigPacketHandler();

    @Override
    public void handle(UpdateConfigPacket packet, PacketSender sender) {
        switch (packet.type) {
            case GUI:
                TaiChiCore.guiConfigs.clear();
                val gson = UpdateConfigPacket.GSON;
                val array = gson.fromJson(packet.jsonData, JsonArray.class);
                for (JsonElement element : array) {
                    val config = gson.fromJson(element, GuiConfig.class);
                    TaiChiCore.guiConfigs.add(config);
                }
        }
    }
}
