package org.figsq.taichicore.taichicore.comm.handler;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.figsq.taichicore.taichicore.TaiChiScreen;
import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenUrlPacket;

public class OpenUrlPacketHandler implements IPacketHandler<OpenUrlPacket, INetHandlerPlayClient> {
    public static final OpenUrlPacketHandler INSTANCE = new OpenUrlPacketHandler();

    @Override
    public void handle(OpenUrlPacket packet, INetHandlerPlayClient sender) {
        val instance = Minecraft.getMinecraft();
        instance.addScheduledTask(()-> instance.displayGuiScreen(new TaiChiScreen(packet.url)));
    }
}
