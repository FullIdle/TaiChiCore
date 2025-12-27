package org.figsq.taichicore.taichicore.common.comm;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public interface IPacket {
    void encode(ByteArrayDataOutput output, CommManager<?> commManager);

    void decode(ByteArrayDataInput input, CommManager<?> commManager);
}
