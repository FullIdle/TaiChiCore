package org.figsq.taichicore.taichicore.screen.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import org.figsq.taichicore.taichicore.screen.gui.scriptable.ScriptableValue;
import org.jetbrains.annotations.NotNull;

import static org.figsq.taichicore.taichicore.screen.gui.scriptable.SBoolean.TRUE;
import static org.figsq.taichicore.taichicore.screen.gui.scriptable.SInteger.ZERO;

public abstract class GuiWidget {
    @NotNull
    public ScriptableValue<Integer> x = ZERO;
    @NotNull
    public ScriptableValue<Integer> y = ZERO;
    @NotNull
    public ScriptableValue<Integer> z = ZERO;
    @NotNull
    public ScriptableValue<Integer> width = ZERO;
    @NotNull
    public ScriptableValue<Integer> height = ZERO;
    @NotNull
    public ScriptableValue<Boolean> visible = TRUE;

    abstract public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta);
}
