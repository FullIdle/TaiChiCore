package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class ChatMessageHandler implements ContextHandler{
    private String message;

    @Override
    public @NotNull String onQuery(CefBrowser browser, CefFrame frame, long queryId, JsonObject source, boolean persistent) {
        val player = Minecraft.getInstance().player;
        if (player == null) throw new RuntimeException("player is null");
        player.connection.sendChat(message);
        return "ok";
    }
}
