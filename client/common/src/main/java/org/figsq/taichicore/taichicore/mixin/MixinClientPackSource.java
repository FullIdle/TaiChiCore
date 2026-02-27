package org.figsq.taichicore.taichicore.mixin;

import net.minecraft.client.resources.ClientPackSource;
import org.figsq.taichicore.taichicore.cef.misc.CefUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPackSource.class)
public class MixinClientPackSource {
    @Inject(
            method = "<clinit>",
            at = @At("HEAD")
    )
    private static void cefInit(CallbackInfo ci) {
        if (!CefUtil.init()) throw new RuntimeException("CEF initialization failed");
    }
}
