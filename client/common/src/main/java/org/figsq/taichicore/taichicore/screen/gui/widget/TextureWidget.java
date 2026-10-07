package org.figsq.taichicore.taichicore.screen.gui.widget;

import lombok.val;
import net.minecraft.client.gui.GuiGraphics;
import org.figsq.taichicore.taichicore.screen.gui.helper.LoggerHelper;
import org.jetbrains.annotations.Nullable;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.awt.*;

public class TextureWidget extends GuiWidget {
    @Nullable
    public CompiledScript texture;

    public TextureWidget(@Nullable CompiledScript texture) {
        this.texture = texture;
    }

    public TextureWidget() {
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int v = -1;

        if (texture != null) try {
            val obj = texture.eval();
            if (obj instanceof Number number) {
                v = number.intValue();
            } else if (obj instanceof String) {
                val str = obj.toString();
                if (str.contains(",")) {
                    val split = str.split(",");
                    if (split.length >= 3)
                        v = new Color(
                                Integer.parseInt(split[0]),
                                Integer.parseInt(split[1]),
                                Integer.parseInt(split[2]),
                                split.length > 3 ? Integer.parseInt(split[3]) : 255
                        ).getRGB();
                } else v = Integer.parseInt(str);
            }
        } catch (ScriptException e) {
            LoggerHelper.error(e);
        }

        try {
            val x = this.x.get();
            val y = this.y.get();
            graphics.fill(x, y, x + this.width.get(), y + this.height.get(), v);
        } catch (ScriptException e) {
            LoggerHelper.error(e);
        }
    }
}
