package org.figsq.taichicore.taichicore.common.comm.packets.common;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.figsq.taichicore.taichicore.common.comm.CommManager;
import org.figsq.taichicore.taichicore.common.comm.IPacket;

//客户端 => 服务器 服务端会更具配置对于的标识头，接受data执行对应内容
// 注意 服务器处理时候，套个try以防万一刷后台~(刷了算你写的菜)
//服务器 => 客户端 客户端会将正在打开和HUD传入对应的标识，和数据而执行
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
