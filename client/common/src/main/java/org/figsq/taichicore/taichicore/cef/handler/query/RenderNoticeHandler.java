package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.cef.scheme.actions.PlayerRenderHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Getter
@Setter
public class RenderNoticeHandler implements QueryHandler {
    public static final RenderNoticeHandler INSTANCE = new RenderNoticeHandler();
    //这里传入的应该都是，渲染通知的函数，来自js且是可持续的
    public final ConcurrentHashMap<Long, NoticeContext> CEF_QUERY_CALLBACKS = new ConcurrentHashMap<>();

    /**
     * 需要在渲染线程上使用
     */
    public void update() {
        CEF_QUERY_CALLBACKS.forEach((queryId, context) -> context.update());
    }

    @Override
    public @Nullable String onQuery(CefBrowser browser, CefFrame frame, long queryId, JsonObject source, boolean persistent, CefQueryCallback callback) {
        if (persistent) throw new RuntimeException("persistent must be true");
        val type = source.get("type").getAsString().toLowerCase();

        val width = Integer.parseInt(source.get("width").getAsString());
        val height = Integer.parseInt(source.get("height").getAsString());
        val scale = Float.parseFloat(source.get("scale").getAsString());
        val rotX = Float.parseFloat(source.get("rotX").getAsString());
        val rotY = Float.parseFloat(source.get("rotY").getAsString());
        val rotZ = Float.parseFloat(source.get("rotZ").getAsString());

        val context = switch (type) {
            case "player" -> new NoticeContext(queryId, callback, () ->
                    Base64.getEncoder().encodeToString(PlayerRenderHelper.renderPlayerToPng(width, height, scale, rotX, rotY, rotZ)));
            default -> throw new RuntimeException("Unknown type!");
        };

        CEF_QUERY_CALLBACKS.put(context.queryId, context);
        return null;
    }

    @Override
    public void onQueryCanceled(CefBrowser browser, CefFrame frame, long queryId) {
        CEF_QUERY_CALLBACKS.remove(queryId);
    }

    public static class NoticeContext {
        public final long queryId;
        public final CefQueryCallback callback;
        private final Supplier<String> displayBase64Getter;

        public NoticeContext(long queryId, CefQueryCallback callback, Supplier<String> displayBase64Getter) {
            this.queryId = queryId;
            this.callback = callback;
            this.displayBase64Getter = displayBase64Getter;
        }

        public void update() {
            callback.success(displayBase64Getter.get());
        }
    }
}
