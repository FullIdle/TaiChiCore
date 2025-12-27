package org.figsq.taichicore.taichicore.comm;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.figsq.taichicore.taichicore.comm.handler.OpenUrlPacketHandler;
import org.figsq.taichicore.taichicore.comm.handler.UpdateConfigPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenUrlPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.client.UpdateConfigPacket;

public class ForgeCommManager extends CommManager<INetHandlerPlayClient> {
    public static final ForgeCommManager INSTANCE = new ForgeCommManager();
    public static final FMLEventChannel network = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL);

    @Override
    public void init() {
        super.init();
        network.register(INSTANCE);
    }

    @Override
    public void registerHandler() {
        registerHandler(UpdateConfigPacket.class, UpdateConfigPacketHandler.INSTANCE);
        registerHandler(OpenUrlPacket.class, OpenUrlPacketHandler.INSTANCE);
    }

    @Override
    public void sendTo(INetHandlerPlayClient target, byte[] bytes) {
        val payload = new PacketBuffer(Unpooled.wrappedBuffer(bytes));
        val pkt = new FMLProxyPacket(payload, CHANNEL);
        network.sendToServer(pkt);
        payload.release();
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        FMLProxyPacket fpp = event.getPacket();
        if (fpp.channel().contains(CHANNEL)) {
            ByteBuf bb = fpp.payload().copy();
            Minecraft.getMinecraft().addScheduledTask(()->{
                val bytes = new byte[bb.readableBytes()];
                bb.readBytes(bytes);
                INSTANCE.receive(event.getHandler(), bytes);
            });
        }
    }
}
