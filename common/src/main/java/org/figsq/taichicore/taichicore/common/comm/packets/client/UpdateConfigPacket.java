package org.figsq.taichicore.taichicore.common.comm.packets.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.IPacket;

public class UpdateConfigPacket implements IPacket {
    public UpdateType type;
    public String jsonData;

    @Override
    public void encode(ByteArrayDataOutput output, CommManager<?> commManager) {
        output.writeByte(type.ordinal());
        output.writeUTF(jsonData);
    }

    @Override
    public void decode(ByteArrayDataInput input, CommManager<?> commManager) {
        type = UpdateType.values()[input.readByte()];
        jsonData = input.readUTF();
    }

    public static final Gson GSON = new GsonBuilder().create();

    public enum UpdateType {
        GUI;
    }
}
