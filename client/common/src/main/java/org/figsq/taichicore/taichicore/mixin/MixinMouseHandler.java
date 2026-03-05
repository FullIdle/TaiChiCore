package org.figsq.taichicore.taichicore.mixin;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @Unique private boolean taiChiCore$_isDragging = false;
    @Unique private int taiChiCore$_dragButton = -1;
    @Unique private double taiChiCore$_lastX, taiChiCore$_lastY;

    @Inject(method = "grabMouse", at = @At("HEAD"), cancellable = true)
    private void onGrabMouse(CallbackInfo ci) {
        if (TaiChiCore.isAltHeld) ci.cancel();
    }

    @Inject(method = "onMove", at = @At("HEAD"))
    private void onMove(long window, double x, double y, CallbackInfo ci) {
        val minecraft = TaiChiCore.HUD.getMinecraft();
        if (!TaiChiCore.renderHUD || minecraft == null || minecraft.screen != null || !TaiChiCore.isAltHeld) return;

        double scale = minecraft.getWindow().getGuiScale();
        double sx = x / scale;
        double sy = y / scale;

        TaiChiCore.HUD.mouseMoved(sx, sy);
        if (taiChiCore$_isDragging) {
            TaiChiCore.HUD.mouseDragged(sx, sy, taiChiCore$_dragButton, sx - taiChiCore$_lastX, sy - taiChiCore$_lastY);
            val browser = TaiChiCore.HUD.getBrowser();
            if (browser != null) browser.onDragOver((int)(sx * scale), (int)(sy * scale), taiChiCore$_dragButton);
        }
        taiChiCore$_lastX = sx;
        taiChiCore$_lastY = sy;
    }

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void onPress(long window, int button, int action, int modifiers, CallbackInfo ci) {
        val minecraft = TaiChiCore.HUD.getMinecraft();
        if (!TaiChiCore.renderHUD || minecraft == null || minecraft.screen != null || !TaiChiCore.isAltHeld) return;

        double scale = minecraft.getWindow().getGuiScale();
        double x = Minecraft.getInstance().mouseHandler.xpos() / scale;
        double y = Minecraft.getInstance().mouseHandler.ypos() / scale;

        if (action == GLFW.GLFW_PRESS) {
            TaiChiCore.HUD.mouseClicked(x, y, button);
            taiChiCore$_isDragging = true;
            taiChiCore$_dragButton = button;
            taiChiCore$_lastX = x;
            taiChiCore$_lastY = y;
        } else if (action == GLFW.GLFW_RELEASE) {
            TaiChiCore.HUD.mouseReleased(x, y, button);
            taiChiCore$_isDragging = false;
            taiChiCore$_dragButton = -1;
        }
        ci.cancel();
    }

    @Inject(method = "onScroll", at = @At("HEAD"))
    private void onScroll(long window, double x, double y, CallbackInfo ci) {
        val minecraft = TaiChiCore.HUD.getMinecraft();
        if (!TaiChiCore.renderHUD || minecraft == null || minecraft.screen != null || !TaiChiCore.isAltHeld) return;

        double scale = minecraft.getWindow().getGuiScale();
        TaiChiCore.HUD.mouseScrolled(
                Minecraft.getInstance().mouseHandler.xpos() / scale,
                Minecraft.getInstance().mouseHandler.ypos() / scale,
                x, y
        );
    }
}