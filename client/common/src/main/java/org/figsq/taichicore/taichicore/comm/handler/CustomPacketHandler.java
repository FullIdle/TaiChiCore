package org.figsq.taichicore.taichicore.comm.handler;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.packets.common.CustomPacket;
import org.figsq.taichicore.taichicore.screen.TaiChiScreen;

//接受 服务端发送来的自定义数据包
//对hud和正在打开的screen执行js
//注意: 不引入 taichi.js 的情况下，脚本应该不会执行生效
public class CustomPacketHandler implements IPacketHandler<CustomPacket, Object> {
    public static final CustomPacketHandler INSTANCE = new CustomPacketHandler();

    @Override
    public void handle(CustomPacket packet, Object sender) {
        val code = "handlerTaiChiCustomPacket(\"" + packet.identifier + "\", \"" + packet.data + "\");";
        val screen = Minecraft.getInstance().screen;
        if (screen instanceof TaiChiScreen) {
            val browser = ((TaiChiScreen) screen).getBrowser();
            if (browser != null) browser.executeJavaScript(code, "", 0);
        }
        //HUD
        val browser = TaiChiCore.HUD.getBrowser();
        if (browser != null) browser.executeJavaScript(code, "", 0);
    }
}
