package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.jetbrains.annotations.Nullable;

public class SendCommandHandler implements QueryHandler{
    public static final SendCommandHandler INSTANCE = new SendCommandHandler();

    @Override
    public @Nullable String onQuery(CefBrowser browser, CefFrame frame, long queryId, JsonObject source, boolean persistent, CefQueryCallback callback) {
        val command = source.get("command").getAsString();
        val player = Minecraft.getInstance().player;
        if (player == null) throw new RuntimeException("player is null");
        player.connection.sendCommand(command);
        return "ok";
    }
}
