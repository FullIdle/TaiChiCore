package org.figsq.taichicore.taichicore.cef.queries;

import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.cef.QueryHandler;

public class CloseHandler implements QueryHandler {
    public static final CloseHandler INSTANCE = new CloseHandler();

    @Override
    public boolean match(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        return request.equalsIgnoreCase("close()");
    }

    @Override
    public boolean handle(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(null));
        return true;
    }
}
