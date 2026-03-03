package org.figsq.taichicore.taichicore.cef.handler.query;

import lombok.Getter;
import lombok.Setter;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.figsq.taichicore.taichicore.comm.ModCommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.server.CustomPacket;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SendCustomPacketHandler implements ContextHandler {
    private String identifier;
    private String data;

    @Override
    public @NotNull String onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent) {
        ModCommManager.INSTANCE.sendTo(null, new CustomPacket(identifier, data));
        return "ok";
    }
}
