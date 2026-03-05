package org.figsq.taichicore.taichicore.cef.scheme.actions;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import static org.lwjgl.opengl.GL11.*;

public final class RenderHelper {

    private RenderHelper() {
    }

    // ── 公用：创建FBO ────────────────────────────────────────────────────────
    private static RenderTarget createFbo(int width, int height) {
        RenderTarget fbo = new TextureTarget(width, height, true, Minecraft.ON_OSX);
        fbo.setClearColor(0f, 0f, 0f, 0f);
        fbo.clear(Minecraft.ON_OSX);
        fbo.bindWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        Matrix4f projection = new Matrix4f().ortho(
                -width / 2f, width / 2f,
                -height / 2f, height / 2f,
                -1000f, 1000f
        );
        RenderSystem.setProjectionMatrix(projection, com.mojang.blaze3d.vertex.VertexSorting.ORTHOGRAPHIC_Z);
        return fbo;
    }

    // ── 公用：读取像素 ────────────────────────────────────────────────────────
    private static ByteBuffer readPixels(RenderTarget fbo, int width, int height) {
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 4);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);
        fbo.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        fbo.destroyBuffers();
        flipVertically(pixelBuffer, width, height);
        return pixelBuffer;
    }

    // ── 公用：垂直翻转 ────────────────────────────────────────────────────────
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

    // ── 公用：编码PNG ────────────────────────────────────────────────────────
    private static byte[] encodePng(ByteBuffer buffer, int width, int height) {
        com.mojang.blaze3d.platform.NativeImage image =
                new com.mojang.blaze3d.platform.NativeImage(
                        com.mojang.blaze3d.platform.NativeImage.Format.RGBA,
                        width, height, false
                );
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

    // ── 公用：确保在渲染线程执行 ─────────────────────────────────────────────
    private static byte[] ensureRenderThread(java.util.function.Supplier<byte[]> task) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isSameThread() && !RenderSystem.isOnRenderThread()) {
            return CompletableFuture.supplyAsync(task, mc).join();
        }
        return task.get();
    }

    // ── 渲染玩家 ─────────────────────────────────────────────────────────────
    public static byte[] renderPlayerToPng(int width, int height, float entitySize) {
        return renderPlayerToPng(width, height, entitySize, 0f, 0f, 0f);
    }

    public static byte[] renderPlayerToPng(int width, int height, float entitySize,
                                           float rotX, float rotY, float rotZ) {
        return ensureRenderThread(() -> doRenderPlayer(width, height, entitySize, rotX, rotY, rotZ));
    }

    private static byte[] doRenderPlayer(int width, int height, float entitySize,
                                         float rotX, float rotY, float rotZ) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return new byte[0];

        float savedYRot = player.getYRot();
        float savedYRotO = player.yRotO;
        float savedXRot = player.getXRot();
        float savedXRotO = player.xRotO;
        float savedYHeadRot = player.getYHeadRot();
        float savedYHeadRotO = player.yHeadRotO;
        float savedYBodyRot = player.yBodyRot;
        float savedYBodyRotO = player.yBodyRotO;

        player.setYRot(0f);
        player.yRotO = 0f;
        player.setXRot(0f);
        player.xRotO = 0f;
        player.setYHeadRot(0f);
        player.yHeadRotO = 0f;
        player.yBodyRot = 0f;
        player.yBodyRotO = 0f;

        RenderTarget fbo = createFbo(width, height);

        float baseScale = height * 0.5f * entitySize;
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0, -height * 0.5f, -100f);
        poseStack.scale(baseScale, baseScale, baseScale);
        poseStack.mulPose(Axis.XP.rotationDegrees(5f + rotX));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotZ));

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        boolean renderShadow = dispatcher.shouldRenderShadow;
        dispatcher.setRenderShadow(false);
        dispatcher.render(player, 0d, 0d, 0d, 0f, 1f, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
        bufferSource.endBatch();
        dispatcher.setRenderShadow(renderShadow);

        player.setYRot(savedYRot);
        player.yRotO = savedYRotO;
        player.setXRot(savedXRot);
        player.xRotO = savedXRotO;
        player.setYHeadRot(savedYHeadRot);
        player.yHeadRotO = savedYHeadRotO;
        player.yBodyRot = savedYBodyRot;
        player.yBodyRotO = savedYBodyRotO;

        return encodePng(readPixels(fbo, width, height), width, height);
    }

    // ── 渲染物品 ─────────────────────────────────────────────────────────────
    public static byte[] renderItemToPng(ItemStack itemStack, int size) {
        return ensureRenderThread(() -> doRenderItem(itemStack, size));
    }

    // 渲染背包指定格子
    public static byte[] renderInventorySlotToPng(int slot, int size) {
        return ensureRenderThread(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return new byte[0];
            ItemStack stack = mc.player.getInventory().getItem(slot);
            return doRenderItem(stack, size);
        });
    }

    private static byte[] doRenderItem(ItemStack itemStack, int size) {
        Minecraft mc = Minecraft.getInstance();
        if (itemStack == null || itemStack.isEmpty()) return new byte[0];

        RenderTarget fbo = createFbo(size, size);

        PoseStack poseStack = new PoseStack();
        poseStack.scale(size, size, size);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        mc.getItemRenderer().renderStatic(
                itemStack,
                ItemDisplayContext.GUI,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                mc.level,
                0
        );

        bufferSource.endBatch();

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(size * size * 4);
        glReadPixels(0, 0, size, size, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);

        fbo.unbindWrite();
        mc.getMainRenderTarget().bindWrite(true);
        fbo.destroyBuffers();

        flipVertically(pixelBuffer, size, size);
        return encodePng(pixelBuffer, size, size);
    }
}