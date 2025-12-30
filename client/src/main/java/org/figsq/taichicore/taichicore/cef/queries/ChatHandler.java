package org.figsq.taichicore.taichicore.cef.queries;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.cef.QueryHandler;
import org.figsq.taichicore.taichicore.cef.TaiChiMessageRouterHandlerAdapter;

public class ChatHandler implements QueryHandler {
    public static final ChatHandler INSTANCE = new ChatHandler();

    @Override
    public boolean match(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        return TaiChiMessageRouterHandlerAdapter.parseArgs(request, "chat") != null;
    }

    @Override
    public boolean handle(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            val args = TaiChiMessageRouterHandlerAdapter.parseArgs(request, "chat");
            Minecraft.getInstance().getConnection().sendChat(args[0]);
        } catch (Exception ignored) {}
        return true;
    }
}
