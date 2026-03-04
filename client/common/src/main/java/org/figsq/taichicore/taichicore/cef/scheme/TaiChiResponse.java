package org.figsq.taichicore.taichicore.cef.scheme;

import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
public class TaiChiResponse {
    private final int statusCode;
    private final String mimeType;
    private final byte[] data;

    private TaiChiResponse(int statusCode, String mimeType, byte[] data) {
        this.statusCode = statusCode;
        this.mimeType = mimeType;
        this.data = data != null ? data : new byte[0];
    }

    // ── 工厂方法 ──────────────────────────────────────────────────────────

    /**
     * 返回 JSON，状态 200
     */
    public static TaiChiResponse json(String json) {
        return new TaiChiResponse(200, MimeTypes.fromExtension("json"), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 返回纯文本，状态 200
     */
    public static TaiChiResponse text(String text) {
        return new TaiChiResponse(200, MimeTypes.fromExtension("txt") + "; charset=utf-8", text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 返回 HTML，状态 200
     */
    public static TaiChiResponse html(String html) {
        return new TaiChiResponse(200, MimeTypes.fromExtension("html") + "; charset=utf-8", html.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 返回二进制内容，MIME type 由文件名自动推断
     * <p>例如：{@code binary(data, "icon.png")} → {@code "image/png"}
     */
    public static TaiChiResponse binary(byte[] data, String filename) {
        return new TaiChiResponse(200, MimeTypes.fromFilename(filename, MimeTypes.DEFAULT_BINARY), data);
    }

    /**
     * 返回二进制内容，显式指定 MIME type
     * <p>无法从文件名推断，或需要覆盖时使用
     */
    public static TaiChiResponse binary(byte[] data, String filename, String fallbackMimeType) {
        return new TaiChiResponse(200, MimeTypes.fromFilename(filename, fallbackMimeType), data);
    }

    /**
     * 无内容返回，fire & forget 场景，状态 204
     */
    public static TaiChiResponse noContent() {
        return new TaiChiResponse(204, MimeTypes.DEFAULT_TEXT, null);
    }

    /**
     * action 未注册，状态 404
     */
    public static TaiChiResponse notFound(String action) {
        return new TaiChiResponse(404, MimeTypes.DEFAULT_TEXT,
                ("No handler registered for action: " + action).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 处理过程发生异常，状态 500
     */
    public static TaiChiResponse error(String message) {
        return new TaiChiResponse(500, MimeTypes.DEFAULT_TEXT,
                message.getBytes(StandardCharsets.UTF_8));
    }
}