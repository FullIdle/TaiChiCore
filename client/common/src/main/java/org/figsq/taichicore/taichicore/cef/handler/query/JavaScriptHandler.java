package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class JavaScriptHandler implements QueryHandler {
    public static final JavaScriptHandler INSTANCE = new JavaScriptHandler();

    @Override
    public @NotNull String onQuery(CefBrowser browser, CefFrame frame, long queryId, JsonObject source, boolean persistent, CefQueryCallback callback) {
        val script = source.get("script").getAsString();
        if (script == null || script.isEmpty()) throw new RuntimeException("script is null");
        try {
            return String.valueOf(TaiChiCore.INSTANCE.evalScript(script));
        } catch (Exception e) {
            throw new RuntimeException("eval script error " + e.getMessage());
        }
    }
}
