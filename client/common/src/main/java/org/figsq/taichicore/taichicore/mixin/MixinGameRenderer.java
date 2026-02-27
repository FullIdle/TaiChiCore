package org.figsq.taichicore.taichicore.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.figsq.taichicore.taichicore.cef.misc.CefUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(at = @At("HEAD"), method = "render")
    public void preRender(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        CefUtil.getCefAppInstance().N_DoMessageLoopWork();
    }
}
