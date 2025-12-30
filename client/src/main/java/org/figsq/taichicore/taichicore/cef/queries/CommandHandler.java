package org.figsq.taichicore.taichicore.cef.queries;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.cef.QueryHandler;
import org.figsq.taichicore.taichicore.cef.TaiChiMessageRouterHandlerAdapter;

public class CommandHandler implements QueryHandler {
    public static final CommandHandler INSTANCE = new CommandHandler();

    @Override
    public boolean match(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        return TaiChiMessageRouterHandlerAdapter.parseArgs(request, "command") != null;
    }

    @Override
    public boolean handle(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            val args = TaiChiMessageRouterHandlerAdapter.parseArgs(request, "command");
            Minecraft.getInstance().getConnection().sendCommand(args[0]);
        } catch (Exception ignored) {}
        return true;
    }
}
