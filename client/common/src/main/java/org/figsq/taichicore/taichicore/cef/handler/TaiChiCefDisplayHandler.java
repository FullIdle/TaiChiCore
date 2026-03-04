package org.figsq.taichicore.taichicore.cef.handler;

import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.figsq.taichicore.taichicore.TaiChiCore;

public class TaiChiCefDisplayHandler extends CefDisplayHandlerAdapter {
    public static final TaiChiCefDisplayHandler INSTANCE = new TaiChiCefDisplayHandler();

    @Override
    public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
        TaiChiCore.LOGGER.info("[CEF] [{}:{}] {}", source, line, message);
        return true;
    }
}