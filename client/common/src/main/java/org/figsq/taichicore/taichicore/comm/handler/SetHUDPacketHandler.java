package org.figsq.taichicore.taichicore.comm.handler;

import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.packets.client.SetHUDPacket;

public class SetHUDPacketHandler implements IPacketHandler<SetHUDPacket, Object> {
    public static final SetHUDPacketHandler INSTANCE = new SetHUDPacketHandler();

    @Override
    public void handle(SetHUDPacket packet, Object sender) {
/*        val url = packet.url;
        Minecraft.getInstance().execute(()-> TaiChiCore.setHudScreen(new TaiChiScreen(url, null)));
    */}
}
