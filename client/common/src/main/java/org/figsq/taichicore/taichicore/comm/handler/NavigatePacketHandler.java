package org.figsq.taichicore.taichicore.comm.handler;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.packets.client.NavigatePacket;
import org.figsq.taichicore.taichicore.screen.TaiChiScreen;

public class NavigatePacketHandler implements IPacketHandler<NavigatePacket, Object> {
    public static final NavigatePacketHandler INSTANCE = new NavigatePacketHandler();

    @Override
    public void handle(NavigatePacket packet, Object sender) {
        val minecraft = Minecraft.getInstance();
        minecraft.execute(()->{
            if (minecraft.player == null) return;
            val screen = minecraft.screen;
            if (screen instanceof TaiChiScreen) {
                val browser = ((TaiChiScreen) screen).getBrowser();
                if (browser != null) {
                    browser.loadURL(packet.url);
                    return;
                }
            }
            if (packet.force) minecraft.setScreen(new TaiChiScreen(packet.url));
        });
    }
}
