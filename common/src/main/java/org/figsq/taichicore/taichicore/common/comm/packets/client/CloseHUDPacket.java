package org.figsq.taichicore.taichicore.common.comm.packets.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.IPacket;

public class CloseHUDPacket implements IPacket {
    public CloseHUDPacket(){}

    @Override
    public void encode(ByteArrayDataOutput output, CommManager<?> commManager) {
    }

    @Override
    public void decode(ByteArrayDataInput input, CommManager<?> commManager) {
    }
}
