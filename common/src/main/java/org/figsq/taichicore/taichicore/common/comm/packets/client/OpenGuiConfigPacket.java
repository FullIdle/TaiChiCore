package org.figsq.taichicore.taichicore.common.comm.packets.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.IPacket;

public class OpenGuiConfigPacket implements IPacket {
    public String identifier;

    public OpenGuiConfigPacket(){}
    public OpenGuiConfigPacket(String identifier){
        this.identifier = identifier;
    }

    @Override
    public void encode(ByteArrayDataOutput output, CommManager<?> commManager) {
        output.writeUTF(identifier);
    }

    @Override
    public void decode(ByteArrayDataInput input, CommManager<?> commManager) {
        identifier = input.readUTF();
    }
}
