package org.figsq.taichicore.taichicore.cef.queries;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.cef.QueryHandler;
import org.figsq.taichicore.taichicore.cef.TaiChiMessageRouterHandlerAdapter;
import org.figsq.taichicore.taichicore.comm.FabricCommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.server.CustomPacket;

public class SendHandler implements QueryHandler {
    public static final SendHandler INSTANCE = new SendHandler();

    @Override
    public boolean match(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        return TaiChiMessageRouterHandlerAdapter.parseArgs(request, "send") != null;
    }

    @Override
    public boolean handle(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            val args = TaiChiMessageRouterHandlerAdapter.parseArgs(request, "send");
            val identity = args[0];
            val data = args[1];
            FabricCommManager.INSTANCE.sendTo(null, new CustomPacket(identity, data));
            callback.success("ok");
        } catch (Exception e) {
            callback.failure(0,e.getMessage());
        }
        return true;
    }
}
