package org.figsq.taichicore.taichicore.cef;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandlerAdapter;

import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;

public class TaiChiLoadHandlerAdapter extends CefLoadHandlerAdapter {
    public static final TaiChiLoadHandlerAdapter INSTANCE = new TaiChiLoadHandlerAdapter();
    public static final CompletableFuture<Void> delaySetting = CompletableFuture.runAsync(() -> {
    });
    public static WeakHashMap<CefBrowser, Boolean> waitWHM = new WeakHashMap<>();

    public static void register(CefClient client) {
        client.removeLoadHandler();
        client.addLoadHandler(INSTANCE);
    }

    @Override
    public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
        try {
            delaySetting.cancel(true);
        } catch (Exception ignored) {
        }
        delaySetting.thenRunAsync(() -> {
            try {
                Thread.sleep(50);
                waitWHM.put(browser, isLoading);
            } catch (InterruptedException ignored) {
            }
        });
    }
}
