package org.figsq.taichicore.taichicore.cef.queries;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.cef.QueryHandler;
import org.figsq.taichicore.taichicore.cef.TaiChiMessageRouterHandlerAdapter;

import javax.script.ScriptException;

public class JSHandler implements QueryHandler {
    public static final JSHandler INSTANCE = new JSHandler();

    @Override
    public boolean match(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        return TaiChiMessageRouterHandlerAdapter.parseArgs(request, "js") != null;
    }

    @Override
    public boolean handle(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        /*val args = TaiChiMessageRouterHandlerAdapter.parseArgs(request, "js");
        assert args != null;
        val script = args[0];
        try {
            TaiChiCore.SCRIPT_ENGINE.eval(script);
            return true;
        } catch (ScriptException e) {
            val player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(Component.literal(e.getMessage()));
                return true;
            }
            e.printStackTrace();
        }*/
        return true;
    }
}
