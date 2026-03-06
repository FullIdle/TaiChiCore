package org.figsq.taichicore.taichicore.common.comm;

import com.google.common.io.ByteStreams;
import lombok.val;
import org.figsq.taichicore.taichicore.common.comm.packets.client.CleanUpGuiConfigPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenUrlPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.client.UpdateGuiConfigPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.common.CustomPacket;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @param <S> 发送者类型 S -> THIS
 */
public abstract class CommManager<S> {
    public static final String CHANNEL = "taichicore:network";

    private final Map<Integer, Constructor<? extends IPacket>> REGISTER = new HashMap<>();
    private final Map<Class<? extends IPacket>, Integer> REGISTER_CLASS = new HashMap<>();
    private final Map<Class<? extends IPacket>, List<IPacketHandler<? super IPacket, ? super S>>> REGISTERS_HANDLER = new HashMap<>();
    public int nextId = 0;

    /**
     * 注册所有
     */
    public void init(){
        registerPackets();
        registerHandler();
    }

    /**
     * 注册公共的所有的包
     */
    public void registerPackets() {
        registerPacket(UpdateGuiConfigPacket.class);
        registerPacket(CleanUpGuiConfigPacket.class);
        registerPacket(CustomPacket.class);
        registerPacket(OpenUrlPacket.class);
    }


    /**
     * 注册处理器
     */
    public abstract void registerHandler();

    public int registerPacket(Class<? extends IPacket> clazz) {
        try {
            REGISTER.put(nextId, clazz.getConstructor());
            REGISTER_CLASS.put(clazz, nextId);
            return nextId++;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends IPacket> void registerHandler(Class<T> clazz, IPacketHandler<T,S> handler) {
        REGISTERS_HANDLER.computeIfAbsent(clazz, k -> new ArrayList<>()).add(((IPacketHandler<IPacket,S>) handler));
    }

    /**
     * 接收
     * @param bytes 接收的数据
     */
    public void receive(S sender, byte[] bytes) {
        val packet = decode(bytes);
        if (packet != null)
            for (IPacketHandler<? super IPacket, ? super S> handler : REGISTERS_HANDLER.getOrDefault(packet.getClass(), Collections.emptyList()))
                handler.handle(packet, sender);
    }

    /**
     * 编码
     * @param packet 要编码的包
     * @return 编码后的数据
     */
    public byte[] encode(IPacket packet) {
        val output = ByteStreams.newDataOutput();
        val clazz = packet.getClass();
        val id = REGISTER_CLASS.get(clazz);
        if (id == null) throw new RuntimeException("Packet " + clazz.getName() + " not registered");
        output.writeInt(id);
        packet.encode(output, this);
        return output.toByteArray();
    }

    public IPacket decode(byte[] bytes) {
        try {
            val byteArray = ByteStreams.newDataInput(bytes);
            val packet = REGISTER.get(byteArray.readInt()).newInstance();
            packet.decode(byteArray, this);
            return packet;
        } catch (Exception ignored) {}
        return null;
    }

    public void sendTo(S target, IPacket packet) {
        sendTo(target, encode(packet));
    }

    public abstract void sendTo(S target, byte[] bytes);
}
