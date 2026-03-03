package org.figsq.taichicore.taichicore.mixin;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.figsq.taichicore.taichicore.comm.handler.GuiConfigPacketHandler;
import org.figsq.taichicore.taichicore.screen.TaiChiScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Inject(
            method = "setScreen",
            at = @At("RETURN")
    )
    private void setScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof TaiChiScreen) return;
        val guiConfigScreen = GuiConfigPacketHandler.getGuiConfigScreen(screen);
        if (guiConfigScreen == null) return;
        setScreen(guiConfigScreen);
    }
}
