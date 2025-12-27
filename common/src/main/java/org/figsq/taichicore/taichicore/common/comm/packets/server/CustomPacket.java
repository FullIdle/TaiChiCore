package org.figsq.taichicore.taichicore.common.comm.packets.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.IPacket;

public class CustomPacket implements IPacket {
    public String identity;
    public String data;

    public CustomPacket() {}

    public CustomPacket(String identity, String data) {
        this.identity = identity;
        this.data = data;
    }

    @Override
    public void encode(ByteArrayDataOutput output, CommManager<?> commManager) {
        output.writeUTF(identity);
        output.writeUTF(data);
    }

    @Override
    public void decode(ByteArrayDataInput input, CommManager<?> commManager) {
        identity = input.readUTF();
        data = input.readUTF();
    }
}
