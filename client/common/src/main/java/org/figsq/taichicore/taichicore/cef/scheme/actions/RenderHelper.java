package org.figsq.taichicore.taichicore.cef.scheme.actions;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import static org.lwjgl.opengl.GL11.*;

public final class RenderHelper {
    private static void flipVertically(ByteBuffer buffer, int width, int height) {
        int rowBytes = width * 4;
        byte[] top = new byte[rowBytes];
        byte[] bottom = new byte[rowBytes];
        for (int y = 0; y < height / 2; y++) {
            int topPos = y * rowBytes;
            int bottomPos = (height - 1 - y) * rowBytes;
            buffer.position(topPos);
            buffer.get(top);
            buffer.position(bottomPos);
            buffer.get(bottom);
            buffer.position(topPos);
            buffer.put(bottom);
            buffer.position(bottomPos);
            buffer.put(top);
        }
        buffer.rewind();
    }

    private static byte[] encode(ByteBuffer buffer, int width, int height, boolean png) {
        if (png) {
            NativeImage image = new NativeImage(NativeImage.Format.RGBA, width, height, false);
            byte[] raw = new byte[width * height * 4];
            buffer.get(raw);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int i = (y * width + x) * 4;
                    int r = raw[i] & 0xFF;
                    int g = raw[i + 1] & 0xFF;
                    int b = raw[i + 2] & 0xFF;
                    int a = raw[i + 3] & 0xFF;
                    image.setPixelRGBA(x, y, (a << 24) | (b << 16) | (g << 8) | r);
                }
            }
            try {
                return image.asByteArray();
            } catch (Exception e) {
                throw new RuntimeException("Failed to encode PNG", e);
            } finally {
                image.close();
            }
        }


        val bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return bytes;
    }

    private static byte[] ensureRenderThread(java.util.function.Supplier<byte[]> task) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isSameThread() && !RenderSystem.isOnRenderThread()) {
            return CompletableFuture.supplyAsync(task, mc).join();
        }
        return task.get();
    }

    public static byte[] renderPlayer(int width, int height, float entitySize, boolean png, boolean followMouse) {
        return ensureRenderThread(() -> doRenderPlayer(width, height, entitySize, png, followMouse));
    }

    private static byte[] doRenderPlayer(int width, int height, float entitySize, boolean png, boolean followMouse) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return new byte[0];

        float centerX = width / 2f;
        float centerY = height / 2f;

        float p = 0f;
        float q = 0f;

        if (followMouse) {
            double winScale = mc.getWindow().getGuiScale();
            float mouseX = (float) (mc.mouseHandler.xpos() / winScale);
            float mouseY = (float) (mc.mouseHandler.ypos() / winScale);

            float screenW = mc.getWindow().getGuiScaledWidth();
            float screenH = mc.getWindow().getGuiScaledHeight();

            float fboMouseX = (mouseX / screenW) * width;
            float fboMouseY = (mouseY / screenH) * height;

            p = (float) Math.atan((centerX - fboMouseX) / 40.0f);
            q = (float) Math.atan((centerY - fboMouseY) / 40.0f);
        }

        RenderTarget fbo = new TextureTarget(width, height, true, Minecraft.ON_OSX);
        fbo.setClearColor(0f, 0f, 0f, 0f);
        fbo.clear(Minecraft.ON_OSX);
        fbo.bindWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        Matrix4f projection = new Matrix4f().ortho(0, width, height, 0, -1000f, 1000f);
        RenderSystem.setProjectionMatrix(projection, VertexSorting.ORTHOGRAPHIC_Z);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        GuiGraphics guiGraphics = new GuiGraphics(mc, bufferSource);

        int scale = (int) (height * 0.5f * entitySize);

        Quaternionf quaternionf  = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(q * 20.0f * (float)(Math.PI / 180f));
        quaternionf.mul(quaternionf2);

        float savedYBodyRot  = player.yBodyRot;
        float savedYRot      = player.getYRot();
        float savedXRot      = player.getXRot();
        float savedYHeadRot  = player.yHeadRot;
        float savedYHeadRotO = player.yHeadRotO;

        player.yBodyRot = 180f + p * 20f;
        player.setYRot(  180f + p * 40f);
        player.setXRot(       -q * 20f);
        player.yHeadRot  = player.getYRot();
        player.yHeadRotO = player.getYRot();

        float w = player.getScale();
        Vector3f offset = new Vector3f(0f, player.getBbHeight() / 2.0f * w, 0f);

        InventoryScreen.renderEntityInInventory(
                guiGraphics,
                centerX, centerY,
                (float) scale / w,
                offset,
                quaternionf,
                quaternionf2,
                player
        );

        bufferSource.endBatch();

        player.yBodyRot  = savedYBodyRot;
        player.setYRot(savedYRot);
        player.setXRot(savedXRot);
        player.yHeadRot  = savedYHeadRot;
        player.yHeadRotO = savedYHeadRotO;

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 4);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);

        fbo.unbindWrite();
        mc.getMainRenderTarget().bindWrite(true);
        fbo.destroyBuffers();

        flipVertically(pixelBuffer, width, height);
        return encode(pixelBuffer, width, height, png);
    }

    public static byte[] renderItem(ItemStack itemStack, int size, boolean png) {
        return ensureRenderThread(() -> doRenderItem(itemStack, size, png));
    }

    public static byte[] renderInventorySlot(int slot, int size, boolean png) {
        return ensureRenderThread(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return new byte[0];
            ItemStack stack = mc.player.getInventory().getItem(slot);
            return doRenderItem(stack, size, png);
        });
    }

    private static byte[] doRenderItem(ItemStack itemStack, int size, boolean png) {
        Minecraft mc = Minecraft.getInstance();
        if (itemStack == null || itemStack.isEmpty()) return new byte[0];

        final int GUI_SIZE = 16;

        RenderTarget fbo = new TextureTarget(size, size, true, Minecraft.ON_OSX);
        fbo.setClearColor(0f, 0f, 0f, 0f);
        fbo.clear(Minecraft.ON_OSX);
        fbo.bindWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        Matrix4f projection = new Matrix4f().ortho(
                0, GUI_SIZE,
                GUI_SIZE, 0,
                -1000f, 1000f
        );
        RenderSystem.setProjectionMatrix(projection, VertexSorting.ORTHOGRAPHIC_Z);

        Lighting.setupForFlatItems();

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        GuiGraphics guiGraphics = new GuiGraphics(mc, bufferSource);

        guiGraphics.renderItem(itemStack, 0, 0);
        guiGraphics.renderItemDecorations(mc.font, itemStack, 0, 0, null);

        bufferSource.endBatch();

        Lighting.setupFor3DItems();

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(size * size * 4);
        glReadPixels(0, 0, size, size, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);

        fbo.unbindWrite();
        mc.getMainRenderTarget().bindWrite(true);
        fbo.destroyBuffers();

        flipVertically(pixelBuffer, size, size);
        return encode(pixelBuffer, size, size, png);
    }
}