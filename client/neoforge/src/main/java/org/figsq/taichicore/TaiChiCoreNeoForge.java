package org.figsq.taichicore;

import lombok.val;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.comm.ModCommManager;
import org.figsq.taichicore.taichicore.comm.ReceivePacket;

import static org.figsq.taichicore.TaiChiCoreNeoForge.MOD_ID;

@Mod(MOD_ID)
public class TaiChiCoreNeoForge extends TaiChiCore {
    public static final String MOD_ID = "taichicore";

    public TaiChiCoreNeoForge(IEventBus bus) {
        this.init();
        NeoForge.EVENT_BUS.register(this);
        bus.addListener(this::onRegisterPayload);
    }

    private void onRegisterPayload(RegisterPayloadHandlersEvent event) {
        val registrar = event
                .registrar("1")
                .optional()
                .executesOn(HandlerThread.NETWORK);
        registrar.playBidirectional(ReceivePacket.ID, ReceivePacket.CODEC, (a,c)-> ModCommManager.INSTANCE.receive(c.connection(), a.getBytes()));
    }

    /**
     * @see #onRegisterCommands(RegisterCommandsEvent)
     */
    @Override
    public void initPlatformComm() {
    }

    @Override
    public void sendToServer(byte[] bytes) {
        PacketDistributor.sendToServer(new ReceivePacket(bytes));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(CMD);
    }
}
