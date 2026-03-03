package org.figsq.taichicore.taichicore.common.comm.packets.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.IPacket;
import org.figsq.taichicore.taichicore.common.comm.records.GuiConfig;
import org.figsq.taichicore.taichicore.common.util.GsonUtil;

public class UpdateGuiConfigPacket implements IPacket {
    public GuiConfig guiConfig;

    public UpdateGuiConfigPacket(){}
    public UpdateGuiConfigPacket(GuiConfig guiConfig){
        this.guiConfig = guiConfig;
    }

    @Override
    public void encode(ByteArrayDataOutput output, CommManager<?> commManager) {
        output.writeUTF(GsonUtil.getGson().toJson(guiConfig));
    }

    @Override
    public void decode(ByteArrayDataInput input, CommManager<?> commManager) {
        guiConfig = GsonUtil.getGson().fromJson(input.readUTF(), GuiConfig.class);
    }
}
