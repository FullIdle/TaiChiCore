package org.figsq.taichicore.taichicore.comm.handler;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenGuiConfigPacket;

public class OpenGuiConfigPacketHandler implements IPacketHandler<OpenGuiConfigPacket, Object> {
    public static final OpenGuiConfigPacketHandler INSTANCE = new OpenGuiConfigPacketHandler();

    @Override
    public void handle(OpenGuiConfigPacket packet, Object sender) {
        val minecraft = Minecraft.getInstance();
        minecraft.execute(()->{
            if (minecraft.player == null) return;
            val screen = GuiConfigPacketHandler.getGuiConfigScreen(packet.identifier);
            if (screen == null || screen.equals(minecraft.screen)) return;
            minecraft.setScreen(screen);
        });
    }
}
