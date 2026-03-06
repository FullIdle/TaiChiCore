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
import java.util.HashMap;

public class TaiChiCefQueryHandler extends CefMessageRouterHandlerAdapter {
    public static final TaiChiCefQueryHandler INSTANCE = new TaiChiCefQueryHandler();
    public static final CefMessageRouter ROUTER = CefMessageRouter.create(INSTANCE);
    private static final HashMap<String, QueryHandler> HANDLERS = new HashMap<>();

    private TaiChiCefQueryHandler() {
    }

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        val prefix = TaiChiCore.MOD_ID + ":";
        if (!request.startsWith(prefix)) return false;
        val jsonStr = request.substring(prefix.length());
        try {
            val gson = GsonUtil.getGson();
            val jsonObject = gson.fromJson(jsonStr, JsonObject.class);
            val actionName = jsonObject.get("action").getAsString();
            val handler = HANDLERS.get(actionName);
            if (handler == null) {
                callback.failure(-1, "Unknown action name: " + actionName);
                return true;
            }
            try {
                val response = handler.onQuery(browser, frame, queryId, jsonObject, persistent, callback);
                if (response != null) callback.success(response);
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
        HANDLERS.forEach((h, k) -> k.onQueryCanceled(browser, frame, queryId));
    }

    private static void register(QueryHandler handler, String... names) {
        for (String name : names) HANDLERS.put(name, handler);
    }

    static {
        register(
                SendCustomPacketHandler.INSTANCE,
                "customPacket",
                "sendCustomPacket",
                "发送自定义数据包",
                "自定义数据包"
        );
        register(
                JavaScriptHandler.INSTANCE,
                "evalJavaScript",
                "evalJS",
                "js",
                "javascript",
                "js脚本",
                "执行js脚本"
        );
        register(
                ChatMessageHandler.INSTANCE,
                "chat",
                "chatMessage",
                "chatMsg",
                "发送聊天信息",
                "聊天信息"
        );
        register(
                SystemMessageHandler.INSTANCE,
                "systemMessage",
                "message",
                "系统信息",
                "信息",
                "系统提示",
                "提示"
        );
        register(
                RenderNoticeHandler.INSTANCE,
                "renderNotice",
                "渲染通知"
        );
        register(
                CloseScreenHandler.INSTANCE,
                "closeScreen",
                "close",
                "关闭",
                "关闭屏幕",
                "关闭界面"
        );
    }
}
