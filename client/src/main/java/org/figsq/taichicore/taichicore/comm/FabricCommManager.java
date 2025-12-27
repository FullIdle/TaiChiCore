package org.figsq.taichicore.taichicore.comm;

import lombok.val;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.util.Identifier;
import org.figsq.taichicore.taichicore.comm.handler.OpenUrlPacketHandler;
import org.figsq.taichicore.taichicore.comm.handler.UpdateConfigPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenUrlPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.client.UpdateConfigPacket;

public class FabricCommManager extends CommManager<PacketSender> {
    public static final FabricCommManager INSTANCE = new FabricCommManager();
    public static final Identifier CHANNEL_IDENTIFIER = Identifier.tryParse(CHANNEL);

    @Override
    public void init() {
        super.init();
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_IDENTIFIER,
                (client,
                 handler,
                 buf,
                 responseSender) -> {
                    byte[] bytes = new byte[buf.readableBytes()];
                    buf.readBytes(bytes);
                    client.execute(() -> INSTANCE.receive(responseSender, bytes));
                });
    }

    @Override
    public void registerHandler() {
        registerHandler(UpdateConfigPacket.class, UpdateConfigPacketHandler.INSTANCE);
        registerHandler(OpenUrlPacket.class, OpenUrlPacketHandler.INSTANCE);
    }

    @Override
    public void sendTo(PacketSender target, byte[] bytes) {
        val buf = PacketByteBufs.create();
        buf.writeBytes(bytes);
        ClientPlayNetworking.send(CHANNEL_IDENTIFIER, buf);
    }
}
