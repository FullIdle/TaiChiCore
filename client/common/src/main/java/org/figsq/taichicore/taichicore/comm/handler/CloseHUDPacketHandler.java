package org.figsq.taichicore.taichicore.comm.handler;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.packets.client.CloseHUDPacket;

public class CloseHUDPacketHandler implements IPacketHandler<CloseHUDPacket, Object> {
    public static final CloseHUDPacketHandler INSTANCE = new CloseHUDPacketHandler();

    @Override
    public void handle(CloseHUDPacket packet, Object sender) {
        Minecraft.getInstance().execute(()-> TaiChiCore.hudLoadURL("about:blank", false));
    }
}
