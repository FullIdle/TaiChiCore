package org.figsq.taichicore.taichicore.cef.queries;

import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.TaiChiScreen;
import org.figsq.taichicore.taichicore.cef.QueryHandler;
import org.figsq.taichicore.taichicore.cef.TaiChiMessageRouterHandlerAdapter;

public class DisplaySlotHandler implements QueryHandler {
    public static final DisplaySlotHandler INSTANCE = new DisplaySlotHandler();

    @Override
    public boolean match(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        return TaiChiMessageRouterHandlerAdapter.parseArgs(request, "displaySlot") != null;
    }

    @Override
    public boolean handle(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            val minecraft = Minecraft.getMinecraft();
            if (!(minecraft.currentScreen instanceof TaiChiScreen)) {
                callback.failure(1, "Not in Tai Chi Screen");
                return true;
            }
            val screen = (TaiChiScreen) minecraft.currentScreen;
            val parent = screen.parent;
            if (parent == null) {
                callback.failure(1, "No parent");
                return true;
            }
            val args = new DisplayArgs(TaiChiMessageRouterHandlerAdapter.parseArgs(request, "displaySlot"));
            screen.displaySlots.put(args.index, args);
            callback.success("true");
        } catch (Exception e) {
            callback.failure(1, e.getMessage());
        }
        return true;
    }

    public static class DisplayArgs {
        public final int index;
        public final int x;
        public final int y;
        public final float scaleX;
        public final float scaleY;
        public final float depth;

        public DisplayArgs(String[] args) {
            this.index = Integer.parseInt(args[0]);
            this.x = Integer.parseInt(args[1]);
            this.y = Integer.parseInt(args[2]);
            this.scaleX = Float.parseFloat(args[3]);
            this.scaleY = Float.parseFloat(args[4]);
            this.depth = Float.parseFloat(args[5]);
        }
    }
}
