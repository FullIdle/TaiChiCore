package org.figsq.taichicore.taichicore.comm.handler;

import lombok.val;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import org.figsq.taichicore.taichicore.TaiChiScreen;
import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenUrlPacket;

public class OpenUrlPacketHandler implements IPacketHandler<OpenUrlPacket, PacketSender> {
    public static final OpenUrlPacketHandler INSTANCE = new OpenUrlPacketHandler();

    @Override
    public void handle(OpenUrlPacket packet, PacketSender sender) {
        val instance = MinecraftClient.getInstance();
        instance.execute(()-> instance.setScreen(new TaiChiScreen(packet.url,null)));
    }
}
