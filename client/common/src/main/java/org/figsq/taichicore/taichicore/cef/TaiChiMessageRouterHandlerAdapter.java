package org.figsq.taichicore.taichicore.cef;

import lombok.val;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import org.figsq.taichicore.taichicore.cef.queries.*;

import java.util.ArrayList;
import java.util.List;

public class TaiChiMessageRouterHandlerAdapter extends CefMessageRouterHandlerAdapter {
    public static final TaiChiMessageRouterHandlerAdapter INSTANCE = new TaiChiMessageRouterHandlerAdapter();
    public static final CefMessageRouter messageRouter = CefMessageRouter.create(INSTANCE);
    public static final List<QueryHandler> HANDLERS;

    static {
        HANDLERS = new ArrayList<>();
        HANDLERS.add(ClickSlotHandler.INSTANCE);
        HANDLERS.add(DisplaySlotHandler.INSTANCE);
        HANDLERS.add(HideSlotHandler.INSTANCE);
        HANDLERS.add(ChatHandler.INSTANCE);
        HANDLERS.add(CommandHandler.INSTANCE);
    }

    public static void register(CefClient client) {
        client.removeMessageRouter(messageRouter);
        client.addMessageRouter(messageRouter);
    }

    public static String[] parseArgs(String request, String name) {
        val s = name + "(";
        if (!request.startsWith(s) || !request.endsWith(")")) return null;
        val substring = request.substring(s.length(), request.length() - 1);
        if (!substring.contains(",")) return new String[]{substring};
        return substring.split(",");
    }

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        for (QueryHandler handler : HANDLERS)
            if (handler.match(browser, frame, queryId, request, persistent, callback))
                return handler.handle(browser, frame, queryId, request, persistent, callback);
        return false;
    }
}
