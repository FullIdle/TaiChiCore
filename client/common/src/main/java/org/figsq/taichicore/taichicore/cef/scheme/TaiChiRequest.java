package org.figsq.taichicore.taichicore.cef.scheme;

import lombok.Getter;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;

import java.util.Map;

@Getter
public class TaiChiRequest {
    private final String action;
    private final String rawArguments;
    private final String[] arguments;
    private final Map<String, String> params;
    private final CefBrowser browser;
    private final CefFrame frame;

    public TaiChiRequest(String action, String rawArguments, String[] arguments, Map<String, String> params, CefBrowser browser, CefFrame frame) {
        this.action = action;
        this.rawArguments = rawArguments;
        this.arguments = arguments;
        this.params = params;
        this.browser = browser;
        this.frame = frame;
    }

    /**
     * 获取 query 参数，不存在时返回 null
     */
    public String getParam(String key) {
        return params.get(key);
    }

    /**
     * 获取 query 参数，不存在时返回默认值
     */
    public String getParam(String key, String defaultValue) {
        return params.getOrDefault(key, defaultValue);
    }

    /**
     * 判断参数是否存在
     */
    public boolean hasParam(String key) {
        return params.containsKey(key);
    }
}
