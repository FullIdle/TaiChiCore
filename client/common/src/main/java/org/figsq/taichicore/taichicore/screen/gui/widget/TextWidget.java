package org.figsq.taichicore.taichicore.screen.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.figsq.taichicore.taichicore.screen.gui.helper.LoggerHelper;
import org.figsq.taichicore.taichicore.screen.gui.scriptable.SBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptException;

import static org.figsq.taichicore.taichicore.screen.gui.scriptable.SBoolean.FALSE;

public class TextWidget extends GuiWidget{
    @Nullable
    public String text;
    @NotNull
    public SBoolean center = FALSE;

    public TextWidget(@Nullable String text) {
        this.text = text;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (text == null) return;
        try {
            if (center.getOrDefault(false)) {
                graphics.drawCenteredString(Minecraft.getInstance().font, text, x.get(), y.get(), -1);
                return;
            }
            graphics.drawString(Minecraft.getInstance().font, text, x.get(), y.get(), -1);
        } catch (ScriptException e) {
            LoggerHelper.error(e);
        }
    }
}
