package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import lombok.val;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.common.util.GsonUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @see org.figsq.taichicore.taichicore.cef.handler.load.TaiChiCefLoadHandler 可看到加载结束后执行的JS
 */
public class TaiChiCefQueryHandler extends CefMessageRouterHandlerAdapter {
    public static final TaiChiCefQueryHandler INSTANCE = new TaiChiCefQueryHandler();
    public static final CefMessageRouter ROUTER = CefMessageRouter.create(INSTANCE);
    private static final HashMap<String, Class<? extends ContextHandler>> HANDLERS = new HashMap<>();

    private TaiChiCefQueryHandler(){}

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        val prefix = TaiChiCore.MOD_ID + ":";
        if (!request.startsWith(prefix)) return false;
        val jsonStr = request.substring(prefix.length());
        try {
            val gson = GsonUtil.getGson();
            val jsonObject = gson.fromJson(jsonStr, JsonObject.class);
            val actionName = jsonObject.get("action").getAsString();
            val clazz = HANDLERS.get(actionName);
            if (clazz == null) {
                callback.failure(-1, "Unknown action name: " + actionName);
                return true;
            }
            val contextHandler = gson.fromJson(jsonObject, clazz);
            try {
                callback.success(contextHandler.onQuery(browser, frame, queryId, request, persistent));
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                val errorMessage = "Action '" + actionName + "' failed:\n" + sw;
                callback.failure(-1, errorMessage);
                TaiChiCore.LOGGER.error(errorMessage);
            }
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public void onQueryCanceled(CefBrowser browser, CefFrame frame, long queryId) {
    }

    private static void register(Class<? extends ContextHandler> chClazz, String... names) {
        for (String name : names) HANDLERS.put(name, chClazz);
    }

    static {
        register(SendCustomPacketHandler.class, "sendCustomPacket", "发送自定义数据包");
    }
}
