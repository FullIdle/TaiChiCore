package org.figsq.taichicore;

import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
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
    public TaiChiCoreNeoForge(IEventBus bus) {
        this.init();
        NeoForge.EVENT_BUS.register(this);
        bus.addListener(this::onRegisterPayload);
//        bus.addListener(this::onRegisterGuiLayer);
    }

    private void onRegisterPayload(RegisterPayloadHandlersEvent event) {
        val registrar = event
                .registrar("1")
                .optional()
                .executesOn(HandlerThread.NETWORK);
        registrar.playBidirectional(ReceivePacket.ID, ReceivePacket.CODEC, (a,c)-> ModCommManager.INSTANCE.receive(c.connection(), a.getBytes()));
    }

//    private void onRegisterGuiLayer(RegisterGuiLayersEvent event) {
//        val id = ResourceLocation.tryBuild("taichicore", "hub");
//        assert id != null;
//        event.registerAboveAll(id, this::renderHUD);
//    }

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
