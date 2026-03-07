package org.figsq.taichicore.taichicore.common.comm.packets.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.IPacket;

public class NavigatePacket implements IPacket {
    public String url;
    public boolean force;

    public NavigatePacket() {}

    public NavigatePacket(String url, boolean force) {
        this.url = url;
        this.force = force;
    }

    @Override
    public void encode(ByteArrayDataOutput output, CommManager<?> commManager) {
        output.writeUTF(url);
        output.writeBoolean(force);
    }

    @Override
    public void decode(ByteArrayDataInput input, CommManager<?> commManager) {
        url = input.readUTF();
        force = input.readBoolean();
    }
}
