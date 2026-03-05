package org.figsq.taichicore.taichicore.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.figsq.taichicore.taichicore.cef.TaiChiCefUtil;
import org.figsq.taichicore.taichicore.cef.handler.query.RenderNoticeHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(at = @At("HEAD"), method = "render")
    public void preRender(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        if (TaiChiCefUtil.isInitialized()) TaiChiCefUtil.getCefApp().N_DoMessageLoopWork();
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void renderUpdateElement(DeltaTracker deltaTracker, CallbackInfo ci) {
        RenderNoticeHandler.INSTANCE.update();
    }
}
