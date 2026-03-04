package org.figsq.taichicore.taichicore.cef.scheme;

import lombok.Getter;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import org.figsq.taichicore.taichicore.cef.scheme.actions.ModActionHandler;
import org.figsq.taichicore.taichicore.cef.scheme.actions.RenderActionHandler;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Getter
public class TaiChiResourceHandler extends CefResourceHandlerAdapter {
    public static final Map<String, ActionHandler> ACTIONS = new HashMap<>();

    private final CefBrowser browser;
    private final CefFrame frame;
    private final String schemeName;
    private final CefRequest request;

    private TaiChiResponse response;
    private int offset = 0;


    public TaiChiResourceHandler(CefBrowser browser, CefFrame frame,
                                 String schemeName, CefRequest request) {
        this.browser = browser;
        this.frame = frame;
        this.schemeName = schemeName;
        this.request = request;
    }

    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        try {
            val uri = new URI(request.getURL());
            val action = uri.getHost();
            String path = uri.getPath();
            if (path.startsWith("/")) path = path.substring(1);
            val args = path.contains("/") ? new String[]{path} : path.split("/");
            val params = parseQuery(uri.getQuery());
            val req = new TaiChiRequest(action, path, args, params, browser, frame);
            response = get(action).handle(req);
        } catch (Exception e) {
            response = TaiChiResponse.error(e.getMessage());
        }
        callback.Continue();
        return true;
    }

    @Override
    public void getResponseHeaders(CefResponse response,
                                   IntRef responseLength, StringRef redirectUrl) {
        val mimeType = this.response.getMimeType();
        if (mimeType != null) response.setMimeType(mimeType);
        response.setStatus(this.response.getStatusCode());
        responseLength.set(this.response.getData().length);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        byte[] data = response.getData();
        int remaining = data.length - offset;
        if (remaining <= 0) {
            bytesRead.set(0);
            return false;
        }

        int chunk = Math.min(bytesToRead, remaining);
        System.arraycopy(data, offset, dataOut, 0, chunk);
        offset += chunk;
        bytesRead.set(chunk);
        return true;
    }

    public static Map<String, String> parseQuery(String query) {
        val map = new HashMap<String, String>();
        if (query == null) return map;
        if (query.startsWith("?") || query.startsWith("&")) query = query.substring(1);
        if (query.isEmpty()) return map;
        for (String param : query.split("&")) {
            val index = param.indexOf("=");
            if (index == -1) continue;
            val key = param.substring(0, index);
            val value = param.substring(index + 1);
            map.put(key, value);
        }
        return map;
    }

    public static void register(String action, ActionHandler handler) {
        ACTIONS.put(action, handler);
    }

    public static ActionHandler get(String action) {
        return ACTIONS.getOrDefault(action, (req) -> TaiChiResponse.notFound(action));
    }

    static {
        register("mod", ModActionHandler.INSTANCE);
        register("render", RenderActionHandler.INSTANCE);
    }
}
