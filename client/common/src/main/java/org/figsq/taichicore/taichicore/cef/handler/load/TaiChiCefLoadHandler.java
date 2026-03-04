package org.figsq.taichicore.taichicore.cef.handler.load;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefRequest;

public class TaiChiCefLoadHandler extends CefLoadHandlerAdapter {
    public static final TaiChiCefLoadHandler INSTANCE = new TaiChiCefLoadHandler();

    private TaiChiCefLoadHandler(){}

    /**
     * 为 {@link org.figsq.taichicore.taichicore.cef.handler.query.TaiChiCefQueryHandler} 用的
     */
    private static final String TAI_CHI_JS_FUNCTION = """
            window.taichi = function(params) {
                return new Promise((resolve, reject) => {
                    window.cefQuery({
                        request: 'taichicore:' + JSON.stringify(params),
                        onSuccess: resolve,
                        onFailure: (code, msg) => reject({code, msg})
                    });
                });
            };""";

    @Override
    public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
        if (!frame.isMain()) return;
        System.out.println("exec");
        browser.executeJavaScript(TAI_CHI_JS_FUNCTION, frame.getURL(), 0);
        super.onLoadStart(browser, frame, transitionType);
    }
}
