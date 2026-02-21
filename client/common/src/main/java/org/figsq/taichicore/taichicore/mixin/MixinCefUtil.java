package org.figsq.taichicore.taichicore.mixin;

import com.google.common.collect.Sets;
import lombok.val;
import org.cef.browser.CefBrowser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Arrays;

@Mixin(
        targets = "com.cinemamod.mcef.CefUtil",
        remap = false
)
public class MixinCefUtil {
    @ModifyArg(
            method = "init()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/cef/CefApp;startup([Ljava/lang/String;)Z"
            ),
            index = 0
    )
    private static String[] modifyCefSwitches(String[] cefSwitches) {
        val newSwitches = Sets.newHashSet(cefSwitches);


        String[] customSwitches = {
                "--disable-gpu-vsync",
                "--disable-frame-rate-limit",
                "--off-screen-frame-rate=60",
                "--enable-begin-frame-scheduling",
                "--enable-gpu-rasterization",
                "--enable-zero-copy",
                "--enable-gpu-compositing",
                "--disable-background-timer-throttling",
                "--disable-renderer-backgrounding"
        };
        newSwitches.addAll(Arrays.asList(customSwitches));
        newSwitches.remove("--disable-gpu");
        newSwitches.remove("--disable-gpu-compositing");
        return newSwitches.toArray(new String[0]);
    }
}
