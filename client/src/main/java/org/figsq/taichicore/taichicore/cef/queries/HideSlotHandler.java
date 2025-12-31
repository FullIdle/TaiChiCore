package org.figsq.taichicore.taichicore.cef.queries;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.TaiChiScreen;
import org.figsq.taichicore.taichicore.cef.QueryHandler;
import org.figsq.taichicore.taichicore.cef.TaiChiMessageRouterHandlerAdapter;

public class HideSlotHandler implements QueryHandler {
    public static final HideSlotHandler INSTANCE = new HideSlotHandler();

    @Override
    public boolean match(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        return TaiChiMessageRouterHandlerAdapter.parseArgs(request, "hideSlot") != null;
    }

    @Override
    public boolean handle(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            val currentScreen = Minecraft.getInstance().screen;
            if (!(currentScreen instanceof TaiChiScreen)) {
                callback.failure(1, "Not on TaiChiScreen");
                return true;
            }
            val screen = (TaiChiScreen) currentScreen;
            val parent = screen.parent;
            if (parent == null) {
                callback.failure(1, "No parent");
                return true;
            }
            val index = Integer.parseInt(TaiChiMessageRouterHandlerAdapter.parseArgs(request, "hideSlot")[0]);
            screen.displaySlots.remove(index);
            callback.success("ok");
            return true;
        }catch (Exception e) {
            callback.failure(1, e.getMessage());
            return true;
        }
    }
}
