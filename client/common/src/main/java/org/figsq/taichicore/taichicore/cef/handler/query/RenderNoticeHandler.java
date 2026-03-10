package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.pipeline.RenderTarget;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.cef.TaiChiCefBrowser;
import org.figsq.taichicore.taichicore.cef.TaiChiCefUtil;
import org.figsq.taichicore.taichicore.screen.RenderHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.mojang.blaze3d.systems.RenderSystem.glDeleteBuffers;

@Getter
@Setter
public class RenderNoticeHandler implements QueryHandler {
    public static final RenderNoticeHandler INSTANCE = new RenderNoticeHandler();

    //用来创建通知上下文获取base64图片编码字符串的函数工厂
    @FunctionalInterface
    public interface RenderTypeFactory {
        Function<NoticeContext, String> create(JsonObject source);
    }

    private static final Map<String, RenderTypeFactory> TYPE_REGISTRY = new HashMap<>();

    static {
        TYPE_REGISTRY.put("entity", source -> {
            int width = Integer.parseInt(source.get("width").getAsString());
            int height = Integer.parseInt(source.get("height").getAsString());
            float scale = Float.parseFloat(source.get("scale").getAsString());
            boolean followMouse = source.has("follow_mouse") && Boolean.parseBoolean(source.get("follow_mouse").getAsString());
            int entityId = Integer.parseInt(source.get("entity_id").getAsString());
            return ctx -> Base64.getEncoder().encodeToString(
                    RenderHelper.renderEntity(ctx, entityId, width, height, scale, false, followMouse));
        });

        TYPE_REGISTRY.put("player", source -> {
            int width = Integer.parseInt(source.get("width").getAsString());
            int height = Integer.parseInt(source.get("height").getAsString());
            float scale = Float.parseFloat(source.get("scale").getAsString());
            boolean followMouse = source.has("follow_mouse") && Boolean.parseBoolean(source.get("follow_mouse").getAsString());
            return ctx -> Base64.getEncoder().encodeToString(
                    RenderHelper.renderPlayer(ctx, width, height, scale, false, followMouse));
        });

        TYPE_REGISTRY.put("item", source -> {
            int slot = Integer.parseInt(source.get("slot").getAsString());
            int size = Integer.parseInt(source.get("size").getAsString());
            return ctx -> Base64.getEncoder().encodeToString(
                    RenderHelper.renderInventorySlot(ctx, slot, size, false));
        });
    }

    public void update() {
        for (TaiChiCefBrowser browser : TaiChiCefUtil.getBrowserSet()) browser.renderNotices();
    }

    @Override
    public @Nullable String onQuery(CefBrowser browser, CefFrame frame, long queryId,
                                    JsonObject source, boolean persistent, CefQueryCallback callback) {
        if (!(browser instanceof TaiChiCefBrowser taiChiCefBrowser))
            throw new RuntimeException("browser is not TaiChiCefBrowser");
        if (!source.has("isRegister"))
            throw new RuntimeException("source has no isRegister");

        if (source.get("isRegister").getAsBoolean()) {
            String type = source.get("type").getAsString().toLowerCase();

            RenderTypeFactory factory = TYPE_REGISTRY.get(type);
            if (factory == null) throw new RuntimeException("Unknown render notices type: " + type);

            NoticeContext context = new NoticeContext(taiChiCefBrowser, queryId, factory.create(source));
            taiChiCefBrowser.addRenderNotice(context);
            return String.valueOf(queryId);
        }

        taiChiCefBrowser.removeRenderNotice(source.get("id").getAsLong());
        return "ok";
    }

    public static class NoticeContext {
        public final TaiChiCefBrowser browser;
        public final long queryId;
        private final Function<NoticeContext, String> displayBase64Getter;

        public RenderTarget fbo = null;
        public int[] pboIds = null;

        public NoticeContext(TaiChiCefBrowser browser, long queryId,
                             Function<NoticeContext, String> displayBase64Getter) {
            this.browser = browser;
            this.queryId = queryId;
            this.displayBase64Getter = displayBase64Getter;
        }

        public void update() {
            browser.executeJavaScript(
                    "window.taichiElements?.renders?.['" + queryId + "']?.update('"
                            + displayBase64Getter.apply(this) + "')",
                    "", 0
            );
        }

        public void cleanup() {
            Minecraft.getInstance().execute(()->{
                if (fbo != null) {
                    fbo.destroyBuffers();
                    fbo = null;
                }
                if (pboIds != null) {
                    glDeleteBuffers(pboIds[0]);
                    glDeleteBuffers(pboIds[1]);
                    pboIds = null;
                }
            });
        }
    }
}
