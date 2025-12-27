package org.figsq.taichicore.taichicore.mixin;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFApp;
import com.cinemamod.mcef.MCEFClient;
import com.cinemamod.mcef.ModScheme;
import lombok.val;
import org.figsq.taichicore.taichicore.cef.TaiChiLoadHandlerAdapter;
import org.figsq.taichicore.taichicore.cef.TaiChiMessageRouterHandlerAdapter;
import org.figsq.taichicore.taichicore.cef.TaiChiScheme;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MCEF.class, remap = false)
public class MixinMCEF {
    @Shadow
    private static MCEFClient client;

    @Shadow
    private static MCEFApp app;

    @Inject(
            method = "initialize",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/cef/CefApp;registerSchemeHandlerFactory(Ljava/lang/String;Ljava/lang/String;Lorg/cef/callback/CefSchemeHandlerFactory;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private static void initialize(CallbackInfoReturnable<Boolean> cir) {
        val appHandle = MixinMCEF.app.getHandle();
        appHandle.registerSchemeHandlerFactory("taichi", "", (browser, frame, url, request) -> new TaiChiScheme(request.getURL()));


        val clientHandle = client.getHandle();
        TaiChiMessageRouterHandlerAdapter.register(clientHandle);
        TaiChiLoadHandlerAdapter.register(clientHandle);
    }
}
