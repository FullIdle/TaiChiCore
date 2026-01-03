package org.figsq.taichicore.taichicore.cef.queries;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.TaiChiScreen;
import org.figsq.taichicore.taichicore.cef.QueryHandler;
import org.figsq.taichicore.taichicore.cef.TaiChiMessageRouterHandlerAdapter;

public class SetPropertyHandler implements QueryHandler {
    public static final SetPropertyHandler INSTANCE = new SetPropertyHandler();

    @Override
    public boolean match(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        return TaiChiMessageRouterHandlerAdapter.parseArgs(request, "setProperty") != null;
    }

    @Override
    public boolean handle(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            val args = TaiChiMessageRouterHandlerAdapter.parseArgs(request, "setProperty");
            val type = args[0];
            val value = args[1];
            val screen = (TaiChiScreen) Minecraft.getInstance().screen;
            switch (type) {
                case "renderCursorStack": {
                    screen.renderCursorStack = Boolean.parseBoolean(value);
                    break;
                }
                //TODO 或许应该有更多
            }
            callback.success("ok");
        } catch (Exception e) {
            callback.failure(0, e.getMessage());
        }
        return true;
    }
}
