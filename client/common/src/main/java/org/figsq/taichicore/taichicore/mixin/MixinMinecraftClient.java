package org.figsq.taichicore.taichicore.mixin;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.TaiChiScreen;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        Minecraft.class
)
public abstract class MixinMinecraftClient {
    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(
            method = "setScreen",
            at = @At(value = "TAIL")
    )
    public void setScreen(Screen screen, CallbackInfo ci) {
        if (!(screen instanceof ContainerScreen)) return;
        val containerScreen = (ContainerScreen) screen;
        for (GuiConfig guiConfig : TaiChiCore.guiConfigs)
            if (guiConfig.match(containerScreen.getTitle().getString()))
                setScreen(new TaiChiScreen(guiConfig.url, containerScreen));
    }
}
