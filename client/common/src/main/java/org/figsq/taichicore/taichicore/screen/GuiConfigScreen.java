package org.figsq.taichicore.taichicore.screen;

import lombok.Getter;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.figsq.taichicore.taichicore.common.comm.records.GuiConfig;
import org.jetbrains.annotations.Nullable;

@Getter
public class GuiConfigScreen extends TaiChiScreen {
    private final GuiConfig guiConfig;
    @Nullable
    private final Screen screen;

    public GuiConfigScreen(GuiConfig guiConfig, @Nullable Screen parent) {
        super(guiConfig.url);
        this.guiConfig = guiConfig;
        this.screen = parent;
    }

    public GuiConfigScreen(GuiConfig guiConfig) {
        this(guiConfig, null);
    }

    @Override
    protected void init() {
        /*
        * 当初始化前就是空的，那么这是第一次打开直接避开了，如果并非第一次，则就会判断是否关闭且需要重载而进行重载*/
        val beforeBrowser = this.getBrowser();
        if (beforeBrowser != null && !beforeBrowser.isClosed() && this.guiConfig.reload)
            Minecraft.getInstance().execute(()-> beforeBrowser.loadURL(this.guiConfig.url));
        super.init();
    }

    @Override
    public void onClose() {
        if (!guiConfig.persistent) forceCose();
        if (this.minecraft != null && this.equals(this.minecraft.screen)) this.minecraft.setScreen(null);
    }

    public void forceCose() {
        super.onClose();
    }
}
