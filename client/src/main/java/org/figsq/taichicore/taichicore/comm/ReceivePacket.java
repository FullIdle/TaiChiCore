package org.figsq.taichicore.taichicore.comm;

import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Getter
public class ReceivePacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ReceivePacket> ID = new CustomPacketPayload.Type<>(FabricCommManager.CHANNEL_IDENTIFIER);
    public static final StreamCodec<RegistryFriendlyByteBuf, ReceivePacket> CODEC = StreamCodec.composite(FabricCommManager.PACKET_CODEC,ReceivePacket::getBytes, ReceivePacket::new);
    public byte[] bytes;

    public ReceivePacket(){}
    public ReceivePacket(byte[] bytes){
        this.bytes = bytes;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
