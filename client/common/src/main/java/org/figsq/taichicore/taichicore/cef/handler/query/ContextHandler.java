package org.figsq.taichicore.taichicore.cef.handler.query;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.jetbrains.annotations.NotNull;

/**
 * taichi 任务请求处理器
 * 实现这个接口还需要确保能满足
 * {@link com.google.gson.Gson#fromJson(String, Class)} 的条件
 */
public interface ContextHandler {
    @NotNull
    String onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent);
}
