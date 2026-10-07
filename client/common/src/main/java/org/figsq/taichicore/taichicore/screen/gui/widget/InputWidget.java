package org.figsq.taichicore.taichicore.screen.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.figsq.taichicore.taichicore.screen.gui.helper.LoggerHelper;
import org.figsq.taichicore.taichicore.screen.gui.scriptable.SBoolean;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptException;

import static org.figsq.taichicore.taichicore.screen.gui.scriptable.SBoolean.TRUE;

public class InputWidget extends GuiWidget {
    private final EditBox editBox = new EditBox(Minecraft.getInstance().font, 0, 0, 100, 20, Component.literal(""));

    @NotNull
    public SBoolean bordered = TRUE;

    public InputWidget(String placeholder) {
        editBox.setValue(placeholder);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        try {
            editBox.setPosition(x.get(), y.get());
            editBox.setSize(width.get(), height.get());
            editBox.setBordered(bordered.get());
        } catch (ScriptException e) {
            LoggerHelper.error(e);
        }
        editBox.render(graphics, mouseX, mouseY, delta);
    }
}
