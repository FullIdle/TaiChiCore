package org.figsq.taichicore.taichicore.mixin;

import lombok.val;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    @Unique
    private static final Set<Integer> LIMIT_KEYS = Set.of(
            GLFW.GLFW_KEY_ESCAPE
    );

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void keyPress(long window, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        val minecraft = TaiChiCore.HUD.getMinecraft();
        if (!TaiChiCore.renderHUD || minecraft == null || minecraft.screen != null) return;

        // Alt 键处理
        if (key == GLFW.GLFW_KEY_LEFT_ALT || key == GLFW.GLFW_KEY_RIGHT_ALT) {
            if (action == GLFW.GLFW_PRESS) {
                TaiChiCore.isAltHeld = true;
                Minecraft.getInstance().mouseHandler.releaseMouse();
            } else if (action == GLFW.GLFW_RELEASE) {
                TaiChiCore.isAltHeld = false;
                Minecraft.getInstance().mouseHandler.grabMouse();
            }
            ci.cancel();
            return;
        }

        if (LIMIT_KEYS.contains(key)) return;

        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)
            TaiChiCore.HUD.keyPressed(key, scanCode, modifiers);
        else if (action == GLFW.GLFW_RELEASE)
            TaiChiCore.HUD.keyReleased(key, scanCode, modifiers);
    }

    @Inject(method = "charTyped", at = @At("HEAD"))
    private void onCharTyped(long window, int codePoint, int modifiers, CallbackInfo ci) {
        val minecraft = TaiChiCore.HUD.getMinecraft();
        if (!TaiChiCore.renderHUD || minecraft == null || minecraft.screen != null) return;
        TaiChiCore.HUD.charTyped(((char) codePoint), modifiers);
    }
}