package org.figsq.taichicore.taichicore.cef;

import lombok.Getter;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

@Getter
public class TaiChiScheme implements CefResourceHandler {
    private final String url;

    public TaiChiScheme(String url) {
        this.url = url;
    }

    @Override
    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        return false;
    }

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef intRef, StringRef stringRef) {

    }

    @Override
    public boolean readResponse(byte[] bytes, int i, IntRef intRef, CefCallback cefCallback) {
        return false;
    }

    @Override
    public void cancel() {

    }
}
