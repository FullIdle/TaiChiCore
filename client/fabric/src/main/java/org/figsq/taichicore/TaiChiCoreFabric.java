package org.figsq.taichicore;

import lombok.val;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.comm.ModCommManager;
import org.figsq.taichicore.taichicore.comm.ReceivePacket;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;

public class TaiChiCoreFabric extends TaiChiCore implements ModInitializer {
    public static final ScriptEngine SCRIPT_ENGINE = new NashornScriptEngineFactory().getScriptEngine(TaiChiCoreFabric.class.getClassLoader());

    @Override
    public void onInitialize() {
        this.init();
        CommandRegistrationCallback.EVENT.register((a, b, c) -> a.register(CMD));
        HudRenderCallback.EVENT.register(this::renderHUD);
    }

    @Override
    public void initPlatformComm() {
        PayloadTypeRegistry.playS2C().register(ReceivePacket.ID, ReceivePacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ReceivePacket.ID, (payload, context) -> ModCommManager.INSTANCE.receive(context.responseSender(), payload.getBytes()));
    }

    @Override
    public void sendToServer(byte[] bytes) {
        val buf = PacketByteBufs.create();
        buf.writeBytes(bytes);
        ClientPlayNetworking.send(new ReceivePacket(bytes));
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return SCRIPT_ENGINE;
    }
}
