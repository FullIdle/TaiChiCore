package org.figsq.taichicore.taichicore.cef.queries;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.figsq.taichicore.taichicore.TaiChiScreen;
import org.figsq.taichicore.taichicore.cef.QueryHandler;
import org.figsq.taichicore.taichicore.cef.TaiChiMessageRouterHandlerAdapter;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ItemInfoHandler implements QueryHandler {
    public static final ItemInfoHandler INSTANCE = new ItemInfoHandler();

    @Override
    public boolean match(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        return TaiChiMessageRouterHandlerAdapter.parseArgs(request, "itemInfo") != null;
    }

    @Override
    public boolean handle(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        try {
            val args = TaiChiMessageRouterHandlerAdapter.parseArgs(request, "itemInfo");
            val slot = Integer.parseInt(args[0]);
            val type = args[1];
            val parent = ((TaiChiScreen) Minecraft.getInstance().screen).parent;
            val item = parent.getMenu().getSlot(slot).getItem();
            if (type.equalsIgnoreCase("name")) {
                callback.success(item.getDisplayName().getString());
                return true;
            }

            if (type.equalsIgnoreCase("lore")) {
                val lore = item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL).stream().map(Component::getString).collect(Collectors.toCollection(ArrayList::new)).toString();
                callback.success(lore);
                return true;
            }

            callback.failure(0, "Not implemented yet " + type);
        } catch (Exception e) {
            callback.failure(0, e.getMessage());
        }
        return true;
    }
}
