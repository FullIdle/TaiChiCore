package org.figsq.taichicore.taichicore.comm;

import io.netty.buffer.ByteBuf;
import lombok.val;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.figsq.taichicore.taichicore.comm.handler.OpenUrlPacketHandler;
import org.figsq.taichicore.taichicore.comm.handler.UpdateConfigPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenUrlPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.client.UpdateConfigPacket;
import org.jetbrains.annotations.NotNull;

public class FabricCommManager extends CommManager<PacketSender> {
    public static final FabricCommManager INSTANCE = new FabricCommManager();
    public static final ResourceLocation CHANNEL_IDENTIFIER = ResourceLocation.tryParse(CHANNEL);
    public static final StreamCodec<ByteBuf, byte[]> PACKET_CODEC = new StreamCodec<ByteBuf, byte[]>() {
        public byte @NotNull [] decode(ByteBuf byteBuf) {
            val bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            return bytes;
        }

        public void encode(ByteBuf byteBuf, byte[] bytes) {
            byteBuf.writeBytes(bytes);
        }
    };

    @Override
    public void init() {
        super.init();
        PayloadTypeRegistry.playS2C().register(ReceivePacket.ID, ReceivePacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ReceivePacket.ID, (payload, context) -> receive(context.responseSender(), payload.getBytes()));
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
        ClientPlayNetworking.send(new ReceivePacket(bytes));
    }
}
