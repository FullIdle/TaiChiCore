package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.jetbrains.annotations.Nullable;

public interface QueryHandler {
    /**
     * 返回值为空的时候，则不会主动调用 {@link org.cef.callback.CefQueryCallback#success(String)}
     */
    @Nullable
    String onQuery(CefBrowser browser, CefFrame frame, long queryId, JsonObject source, boolean persistent, CefQueryCallback callback);

    default void onQueryCanceled(CefBrowser browser, CefFrame frame, long queryId) {}
}
