package org.figsq.taichicore.taichicore.mixin;

import lombok.val;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.figsq.taichicore.taichicore.cef.TaiChiCefUtil;
import org.figsq.taichicore.taichicore.cef.scheme.actions.PlayerRenderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Base64;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(at = @At("HEAD"), method = "render")
    public void preRender(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        if (TaiChiCefUtil.isInitialized()) TaiChiCefUtil.getCefApp().N_DoMessageLoopWork();
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void renderUpdateElement(DeltaTracker deltaTracker, CallbackInfo ci) {
        val bytes = PlayerRenderHelper.renderPlayerToPng(100, 100, 1);
        String base64 = Base64.getEncoder().encodeToString(bytes);
        TaiChiCefUtil.getBrowserSet().forEach(b-> b.executeJavaScript(
                "document.querySelectorAll('taichi-render-player')" +
                        ".forEach(el => el.update('" + base64 + "'))",
                "", 0
        ));
    }
}
