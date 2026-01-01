package org.figsq.taichicore;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.figsq.taichicore.taichicore.TaiChiCore;

@Mod("taichicore")
public class TaiChiCoreNeoForge extends TaiChiCore {
    public TaiChiCoreNeoForge() {
        this.init();
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public void initPlatformComm() {

    }

    @Override
    public void sendToServer(byte[] bytes) {

    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(CMD);
    }
}
