package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.cef.TaiChiCefBrowser;
import org.figsq.taichicore.taichicore.cef.TaiChiCefUtil;
import org.figsq.taichicore.taichicore.cef.scheme.actions.RenderHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.function.Supplier;

@Getter
@Setter
public class RenderNoticeHandler implements QueryHandler {
    public static final RenderNoticeHandler INSTANCE = new RenderNoticeHandler();

    /**
     * 需要在渲染线程上使用
     */
    public void update() {
        for (TaiChiCefBrowser browser : TaiChiCefUtil.getBrowserSet()) browser.renderNotices();
    }

    @Override
    public @Nullable String onQuery(CefBrowser browser, CefFrame frame, long queryId, JsonObject source, boolean persistent, CefQueryCallback callback) {
        if (!(browser instanceof TaiChiCefBrowser)) throw new RuntimeException("browser is not TaiChiCefBrowser");
        val taiChiCefBrowser = (TaiChiCefBrowser) browser;

        if (!source.has("isRegister")) throw new RuntimeException("source has no isRegister");
        if (source.get("isRegister").getAsBoolean()) {
            val type = source.get("type").getAsString().toLowerCase();

            val context = new NoticeContext(taiChiCefBrowser, queryId,
                    switch (type) {
                        case "player" -> (Supplier<String>) () ->
                        {
                            val width = Integer.parseInt(source.get("width").getAsString());
                            val height = Integer.parseInt(source.get("height").getAsString());
                            val scale = Float.parseFloat(source.get("scale").getAsString());
                            return Base64.getEncoder().encodeToString(RenderHelper.renderPlayerToPng(
                                    width,
                                    height,
                                    scale
                            ));
                        };
                        case "item" -> (Supplier<String>) () ->
                        {
                            val slot = Integer.parseInt(source.get("slot").getAsString());
                            val size = Integer.parseInt(source.get("size").getAsString());
                            return Base64.getEncoder().encodeToString(RenderHelper.renderInventorySlotToPng(
                                    slot,
                                    size
                            ));
                        };
                        default -> throw new RuntimeException("Unknown render notices type!");
                    }
            );

            taiChiCefBrowser.addRenderNotice(context);
            return String.valueOf(queryId);
        }
        val id = source.get("id").getAsLong();
        taiChiCefBrowser.removeRenderNotice(id);
        return "ok";
    }

    public static class NoticeContext {
        public final TaiChiCefBrowser browser;
        public final long queryId;
        private final Supplier<String> displayBase64Getter;

        public NoticeContext(TaiChiCefBrowser browser, long queryId, Supplier<String> displayBase64Getter) {
            this.browser = browser;
            this.queryId = queryId;
            this.displayBase64Getter = displayBase64Getter;
        }

        public void update() {
            browser.executeJavaScript(
                    "window.taichiElements?.renders?.['" + queryId + "']?.update('" + displayBase64Getter.get() + "')",
                    "", 0
            );
        }
    }
}
