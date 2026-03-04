package org.figsq.taichicore.taichicore.cef.scheme;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * MIME type 工具类
 * 基于 MDN Common MIME types 表
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/MIME_types/Common_types">MDN Common MIME types</a>
 */
public final class MimeTypes {

    /** 无法识别时的默认二进制类型 */
    public static final String DEFAULT_BINARY = "application/octet-stream";

    /** 无法识别时的默认文本类型 */
    public static final String DEFAULT_TEXT = "text/plain";

    private static final Map<String, String> EXT_MAP = new HashMap<>();

    static {
        // ── Text ──────────────────────────────────────────────────────────
        EXT_MAP.put("css",          "text/css");
        EXT_MAP.put("csv",          "text/csv");
        EXT_MAP.put("htm",          "text/html");
        EXT_MAP.put("html",         "text/html");
        EXT_MAP.put("ics",          "text/calendar");
        EXT_MAP.put("js",           "text/javascript");
        EXT_MAP.put("mjs",          "text/javascript");
        EXT_MAP.put("md",           "text/markdown");
        EXT_MAP.put("txt",          "text/plain");
        EXT_MAP.put("xml",          "application/xml");
        EXT_MAP.put("xhtml",        "application/xhtml+xml");

        // ── Image ─────────────────────────────────────────────────────────
        EXT_MAP.put("apng",         "image/apng");
        EXT_MAP.put("avif",         "image/avif");
        EXT_MAP.put("bmp",          "image/bmp");
        EXT_MAP.put("gif",          "image/gif");
        EXT_MAP.put("ico",          "image/vnd.microsoft.icon");
        EXT_MAP.put("jpeg",         "image/jpeg");
        EXT_MAP.put("jpg",          "image/jpeg");
        EXT_MAP.put("png",          "image/png");
        EXT_MAP.put("svg",          "image/svg+xml");
        EXT_MAP.put("tif",          "image/tiff");
        EXT_MAP.put("tiff",         "image/tiff");
        EXT_MAP.put("webp",         "image/webp");

        // ── Audio ─────────────────────────────────────────────────────────
        EXT_MAP.put("aac",          "audio/aac");
        EXT_MAP.put("mid",          "audio/midi");
        EXT_MAP.put("midi",         "audio/midi");
        EXT_MAP.put("mp3",          "audio/mpeg");
        EXT_MAP.put("mpga",         "audio/mpeg");
        EXT_MAP.put("oga",          "audio/ogg");
        EXT_MAP.put("ogg",          "audio/ogg");
        EXT_MAP.put("opus",         "audio/ogg");
        EXT_MAP.put("wav",          "audio/wav");
        EXT_MAP.put("weba",         "audio/webm");

        // ── Video ─────────────────────────────────────────────────────────
        EXT_MAP.put("avi",          "video/x-msvideo");
        EXT_MAP.put("mp4",          "video/mp4");
        EXT_MAP.put("mpeg",         "video/mpeg");
        EXT_MAP.put("mpg",          "video/mpeg");
        EXT_MAP.put("ogv",          "video/ogg");
        EXT_MAP.put("ts",           "video/mp2t");
        EXT_MAP.put("webm",         "video/webm");
        EXT_MAP.put("3gp",          "video/3gpp");
        EXT_MAP.put("3g2",          "video/3gpp2");

        // ── Font ──────────────────────────────────────────────────────────
        EXT_MAP.put("eot",          "application/vnd.ms-fontobject");
        EXT_MAP.put("otf",          "font/otf");
        EXT_MAP.put("ttf",          "font/ttf");
        EXT_MAP.put("woff",         "font/woff");
        EXT_MAP.put("woff2",        "font/woff2");

        // ── Application / Archive ─────────────────────────────────────────
        EXT_MAP.put("7z",           "application/x-7z-compressed");
        EXT_MAP.put("abw",          "application/x-abiword");
        EXT_MAP.put("arc",          "application/x-freearc");
        EXT_MAP.put("azw",          "application/vnd.amazon.ebook");
        EXT_MAP.put("bin",          "application/octet-stream");
        EXT_MAP.put("bz",           "application/x-bzip");
        EXT_MAP.put("bz2",          "application/x-bzip2");
        EXT_MAP.put("cda",          "application/x-cdf");
        EXT_MAP.put("csh",          "application/x-csh");
        EXT_MAP.put("doc",          "application/msword");
        EXT_MAP.put("docx",         "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        EXT_MAP.put("epub",         "application/epub+zip");
        EXT_MAP.put("gz",           "application/gzip");
        EXT_MAP.put("jar",          "application/java-archive");
        EXT_MAP.put("json",         "application/json");
        EXT_MAP.put("jsonld",       "application/ld+json");
        EXT_MAP.put("mpkg",         "application/vnd.apple.installer+xml");
        EXT_MAP.put("odp",          "application/vnd.oasis.opendocument.presentation");
        EXT_MAP.put("ods",          "application/vnd.oasis.opendocument.spreadsheet");
        EXT_MAP.put("odt",          "application/vnd.oasis.opendocument.text");
        EXT_MAP.put("ogx",          "application/ogg");
        EXT_MAP.put("pdf",          "application/pdf");
        EXT_MAP.put("php",          "application/x-httpd-php");
        EXT_MAP.put("ppt",          "application/vnd.ms-powerpoint");
        EXT_MAP.put("pptx",         "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        EXT_MAP.put("rar",          "application/vnd.rar");
        EXT_MAP.put("rtf",          "application/rtf");
        EXT_MAP.put("sh",           "application/x-sh");
        EXT_MAP.put("tar",          "application/x-tar");
        EXT_MAP.put("vsd",          "application/vnd.visio");
        EXT_MAP.put("wasm",         "application/wasm");
        EXT_MAP.put("webmanifest",  "application/manifest+json");
        EXT_MAP.put("xls",          "application/vnd.ms-excel");
        EXT_MAP.put("xlsx",         "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        EXT_MAP.put("xul",          "application/vnd.mozilla.xul+xml");
        EXT_MAP.put("zip",          "application/zip");
    }

    private MimeTypes() {}

    /**
     * 根据文件名或路径推断 MIME type
     * <p>例如：{@code "icon.png"} → {@code "image/png"}，{@code "textures/gui/bg.png"} → {@code "image/png"}
     *
     * @param filename 文件名或路径
     * @return MIME type，无法识别时返回 null
     */
    @Nullable
    public static String fromFilename(String filename) {
        if (filename == null || filename.isEmpty()) return null;
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return null;
        return fromExtension(filename.substring(dot + 1));
    }

    /**
     * 根据文件名或路径推断 MIME type，无法识别时返回默认值
     *
     * @param filename     文件名或路径
     * @param defaultValue 无法识别时的回退值，推荐 {@link #DEFAULT_BINARY}
     */
    public static String fromFilename(String filename, String defaultValue) {
        String mime = fromFilename(filename);
        return mime != null ? mime : defaultValue;
    }

    /**
     * 根据扩展名（不含点，大小写不敏感）推断 MIME type
     * <p>例如：{@code "PNG"} → {@code "image/png"}
     *
     * @param extension 扩展名，如 {@code "png"}、{@code "HTML"}
     * @return MIME type，无法识别时返回 null
     */
    @Nullable
    public static String fromExtension(String extension) {
        if (extension == null || extension.isEmpty()) return null;
        return EXT_MAP.get(extension.toLowerCase());
    }

    /**
     * 判断给定 MIME type 是否为文本类型（可安全作为字符串处理）
     */
    public static boolean isText(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.startsWith("text/")
                || mimeType.equals("application/json")
                || mimeType.equals("application/xml")
                || mimeType.equals("application/xhtml+xml")
                || mimeType.equals("application/ld+json")
                || mimeType.equals("application/manifest+json")
                || mimeType.equals("image/svg+xml");
    }

    /**
     * 判断给定 MIME type 是否为图片类型
     */
    public static boolean isImage(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.startsWith("image/");
    }
}