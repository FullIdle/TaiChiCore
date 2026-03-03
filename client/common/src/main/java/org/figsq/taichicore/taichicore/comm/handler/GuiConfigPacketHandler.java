package org.figsq.taichicore.taichicore.comm.handler;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.figsq.taichicore.taichicore.common.comm.IPacketHandler;
import org.figsq.taichicore.taichicore.common.comm.packets.client.CleanUpGuiConfigPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.client.UpdateGuiConfigPacket;
import org.figsq.taichicore.taichicore.common.comm.records.GuiConfig;
import org.figsq.taichicore.taichicore.screen.GuiConfigScreen;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class GuiConfigPacketHandler {
    public static final IPacketHandler<UpdateGuiConfigPacket, Object> UPDATE = new IPacketHandler<>() {
        @Override
        public void handle(UpdateGuiConfigPacket packet, Object sender) {
            val identifier = packet.guiConfig.identifier;
            if (identifier == null) return;
            val minecraft = Minecraft.getInstance();
            minecraft.execute(() -> {
                val oldEntry = SCREEN_CACHE.put(identifier, new AbstractMap.SimpleEntry<>(packet.guiConfig, null));
                if (oldEntry != null && oldEntry.getValue() != null) oldEntry.getValue().forceCose();

                val guiConfigScreen = getGuiConfigScreen(minecraft.screen);
                if (guiConfigScreen == null) return;
                minecraft.setScreen(guiConfigScreen);
            });
        }
    };

    public static final IPacketHandler<CleanUpGuiConfigPacket, Object> CLEANUP = new IPacketHandler<>() {
        @Override
        public void handle(CleanUpGuiConfigPacket packet, Object sender) {
            Minecraft.getInstance().execute(()->{
                for (Map.Entry<GuiConfig, GuiConfigScreen> entry : SCREEN_CACHE.values()) {
                    val value = entry.getValue();
                    if (value == null) return;
                    value.forceCose();
                }
                SCREEN_CACHE.clear();
            });
        }
    };

    private static final HashMap<String, Map.Entry<GuiConfig, GuiConfigScreen>> SCREEN_CACHE = new HashMap<>();

    public static GuiConfigScreen getGuiConfigScreen(GuiConfig guiConfig) {
        val entry = SCREEN_CACHE.get(guiConfig.identifier);
        if (entry == null) return null;
        return resolveScreen(entry, guiConfig, null);
    }

    /**
     * 判断是否需要给界面套浏览器界面
     * @param screen 需要判断的界面
     * @return 最终的界面 可能为空
     */
    @Nullable
    public static GuiConfigScreen getGuiConfigScreen(Screen screen) {
        if (screen == null) return null;
        val title = screen.getTitle();
        val titleName = title.getString();

        for (val entry : SCREEN_CACHE.values()) {
            val guiConfig = entry.getKey();
            if (guiConfig.matchScript == null || !guiConfig.match(titleName, title)) continue;
            return resolveScreen(entry, guiConfig, screen);
        }
        return null;
    }

    private static GuiConfigScreen resolveScreen(Map.Entry<GuiConfig, GuiConfigScreen> entry, GuiConfig guiConfig, Screen screen) {
        val gcs = entry.getValue();
        val browser = gcs != null ? gcs.getBrowser() : null;
        val browserValid = browser != null && !browser.isClosed();


        if (browserValid) {
            if (guiConfig.persistent) return gcs;
            gcs.forceCose();
        }

        val newGcs = new GuiConfigScreen(guiConfig, screen);
        entry.setValue(newGcs);
        return newGcs;
    }
}
