package org.figsq.taichicore.taichicore.comm;

import io.netty.buffer.ByteBuf;
import lombok.val;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.comm.handler.OpenUrlPacketHandler;
import org.figsq.taichicore.taichicore.comm.handler.UpdateConfigPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenUrlPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.client.UpdateConfigPacket;
import org.jetbrains.annotations.NotNull;

public class ModCommManager extends CommManager<Object> {
    public static final ModCommManager INSTANCE = new ModCommManager();
    public static final ResourceLocation CHANNEL_IDENTIFIER = ResourceLocation.tryParse(CHANNEL);
    public static final StreamCodec<ByteBuf, byte[]> PACKET_CODEC = new StreamCodec<>() {
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
    }

    @Override
    public void registerHandler() {
        registerHandler(UpdateConfigPacket.class, UpdateConfigPacketHandler.INSTANCE);
        registerHandler(OpenUrlPacket.class, OpenUrlPacketHandler.INSTANCE);
    }

    @Override
    public void sendTo(Object target, byte[] bytes) {
        TaiChiCore.INSTANCE.sendToServer(bytes);
    }
}
