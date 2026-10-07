package org.figsq.taichicore.taichicore.screen.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.figsq.taichicore.taichicore.screen.gui.helper.LoggerHelper;
import org.figsq.taichicore.taichicore.screen.gui.widget.GuiWidget;

import javax.script.ScriptException;
import java.util.Comparator;


public class GuiScreen extends Screen {
    public final GuiConfig guiConfig;

    public GuiScreen(GuiConfig guiConfig) {
        super(Component.literal(guiConfig.title));
        this.guiConfig = guiConfig;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        try {
            guiConfig.widgets.sort(Comparator.comparingInt(a -> {
                try {
                    return a.z.get();
                } catch (ScriptException e) {
                    LoggerHelper.error(e);
                    return 0;
                }
            }));
            for (GuiWidget widget : guiConfig.widgets)
                if (widget.visible.get()) widget.render(guiGraphics, mouseX, mouseY, delta);
        } catch (ScriptException e) {
            LoggerHelper.error(e);
        }
    }
}
