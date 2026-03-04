package org.figsq.taichicore.taichicore.mixin;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.figsq.taichicore.taichicore.cef.TaiChiCefUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionInstance.class)
public class MixinOptionInstance<T> {
    @Inject(method = "set", at = @At("HEAD"))
    public void set(T object, CallbackInfo ci) {
        val minecraft = Minecraft.getInstance();
        val options = minecraft.options;
        if (options == null) return;
        val instance = options.framerateLimit();
        if (!this.equals(instance)) return;
        TaiChiCefUtil.updateAllFrameRateLimit(((Integer) object));
    }
}
