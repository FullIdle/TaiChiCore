package org.figsq.taichicore.taichicore.cef.handler.query;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SystemMessageHandler implements QueryHandler {
    public static final SystemMessageHandler INSTANCE = new SystemMessageHandler();

    @Override
    public @NotNull String onQuery(CefBrowser browser, CefFrame frame, long queryId, JsonObject source, boolean persistent, CefQueryCallback callback) {
        val message = source.get("message").getAsString();
        val player = Minecraft.getInstance().player;
        if (player == null) throw new RuntimeException("player is null");
        player.sendSystemMessage(Component.literal(message));
        return "ok";
    }
}
