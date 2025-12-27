package org.figsq.taichicore.taichicore.common.comm.packets.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.IPacket;

public class OpenUrlPacket implements IPacket {
    public String url;

    public OpenUrlPacket() {}

    public OpenUrlPacket(String url) {
        this.url = url;
    }

    @Override
    public void encode(ByteArrayDataOutput output, CommManager<?> commManager) {
        output.writeUTF(url);
    }

    @Override
    public void decode(ByteArrayDataInput input, CommManager<?> commManager) {
        url = input.readUTF();
    }
}
