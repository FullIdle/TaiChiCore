package org.figsq.taichicore.taichicore.mixin;

import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
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
        MinecraftClient.class
)
public abstract class MixinMinecraftClient {
    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Inject(
            method = "setScreen",
            at = @At(value = "TAIL")
    )
    public void setScreen(Screen screen, CallbackInfo ci) {
        if (!(screen instanceof GenericContainerScreen)) return;
        val containerScreen = (GenericContainerScreen) screen;
        for (GuiConfig guiConfig : TaiChiCore.guiConfigs)
            if (guiConfig.match(containerScreen.getTitle().getString()))
                setScreen(new TaiChiScreen(guiConfig.url, containerScreen));
    }
}
