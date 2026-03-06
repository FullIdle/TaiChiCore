package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.comm.ModCommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.common.CustomPacket;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SendCustomPacketHandler implements QueryHandler {
    public static final SendCustomPacketHandler INSTANCE = new SendCustomPacketHandler();

    @Override
    public @NotNull String onQuery(CefBrowser browser, CefFrame frame, long queryId, JsonObject source, boolean persistent, CefQueryCallback callback) {
        val identifier = source.get("identifier").getAsString();
        val data = source.get("data").getAsString();
        ModCommManager.INSTANCE.sendTo(null, new CustomPacket(identifier, data));
        return "ok";
    }
}
