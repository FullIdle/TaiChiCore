package org.figsq.taichicore.taichicore.cef.queries;

import lombok.val;
import net.minecraft.client.MinecraftClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.TaiChiScreen;
import org.figsq.taichicore.taichicore.cef.QueryHandler;
import org.figsq.taichicore.taichicore.cef.TaiChiMessageRouterHandlerAdapter;

public class ClickSlotHandler implements QueryHandler {
    public static final ClickSlotHandler INSTANCE = new ClickSlotHandler();

    @Override
    public boolean match(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        return TaiChiMessageRouterHandlerAdapter.parseArgs(request, "clickSlot") != null;
    }

    @Override
    public boolean handle(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            val args = TaiChiMessageRouterHandlerAdapter.parseArgs(request, "clickSlot");
            if (args == null) {
                callback.failure(0, "Invalid arguments");
                return true;
            }
            val index = Integer.parseInt(args[0]);
            val client = MinecraftClient.getInstance();
            val screen = client.currentScreen;
            if (!(screen instanceof TaiChiScreen)) {
                callback.failure(0, "Not on a TaiChi screen");
                return true;
            }
            val taiChiScreen = (TaiChiScreen) screen;
            val container = taiChiScreen.parent;
            if (container == null) {
                callback.failure(1, "TaiChi screen has a parent");
                return true;
            }
            val slot = container.getScreenHandler().getSlot(index);
            if (slot == null) {
                callback.failure(2, "Invalid slot number");
                return true;
            }
            callback.success(String.valueOf(container.mouseClicked(slot.x + container.x, slot.y + container.y, 0) && container.mouseReleased(slot.x + container.x, slot.y + container.y, 0)));
            return true;
        } catch (Exception ignored) {
            callback.failure(1, "Invalid slot number");
            return true;
        }
    }
}
