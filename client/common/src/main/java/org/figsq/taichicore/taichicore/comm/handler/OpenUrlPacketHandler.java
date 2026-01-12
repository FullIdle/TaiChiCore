package org.figsq.taichicore.taichicore.comm.handler;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.figsq.taichicore.taichicore.TaiChiScreen;
import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenUrlPacket;

public class OpenUrlPacketHandler implements IPacketHandler<OpenUrlPacket, Object> {
    public static final OpenUrlPacketHandler INSTANCE = new OpenUrlPacketHandler();

    @Override
    public void handle(OpenUrlPacket packet, Object sender) {
        val instance = Minecraft.getInstance();
        System.out.println(instance);
        System.out.println("接收了!");
        instance.execute(() -> instance.setScreen(new TaiChiScreen(packet.url, null)));
    }
}
