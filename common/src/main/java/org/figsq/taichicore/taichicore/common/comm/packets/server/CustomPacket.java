package org.figsq.taichicore.taichicore.common.comm.packets.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.IPacket;

public class CustomPacket implements IPacket {
    public String identifier;
    public String data;

    public CustomPacket() {}

    public CustomPacket(String identifier, String data) {
        this.identifier = identifier;
        this.data = data;
    }

    @Override
    public void encode(ByteArrayDataOutput output, CommManager<?> commManager) {
        output.writeUTF(identifier);
        output.writeUTF(data);
    }

    @Override
    public void decode(ByteArrayDataInput input, CommManager<?> commManager) {
        identifier = input.readUTF();
        data = input.readUTF();
    }
}
