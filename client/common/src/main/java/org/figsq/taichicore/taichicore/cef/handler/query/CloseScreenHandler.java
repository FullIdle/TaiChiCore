package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.jetbrains.annotations.Nullable;

public class CloseScreenHandler implements QueryHandler{
    public static final CloseScreenHandler INSTANCE = new CloseScreenHandler();

    @Override
    public @Nullable String onQuery(CefBrowser browser, CefFrame frame, long queryId, JsonObject source, boolean persistent, CefQueryCallback callback) {
        val minecraft = Minecraft.getInstance();
        val screen = minecraft.screen;
        if (screen != null) screen.onClose();
        return "ok";
    }
}
