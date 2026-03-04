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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public final class PlayerRenderHelper {

    private PlayerRenderHelper() {}

    /**
     * 将玩家渲染为透明背景的 PNG 字节数组（使用默认参数）
     *
     * @param width      图像宽度（像素）
     * @param height     图像高度（像素）
     * @param entitySize 实体渲染大小比例（建议范围 0.5~2.0，1.0 为默认）
     * @return PNG 字节数组
     */
    public static byte[] renderPlayerToPng(int width, int height, float entitySize) {
        return renderPlayerToPng(width, height, entitySize, 0f, 0f, 0f);
    }

    /**
     * 将玩家渲染为透明背景的 PNG 字节数组
     * 玩家朝向始终以正面为基准，rotX/rotY/rotZ 是叠加在正面基准上的自定义旋转
     * 自动调度到 MC 渲染线程执行
     *
     * @param width      图像宽度（像素）
     * @param height     图像高度（像素）
     * @param entitySize 实体渲染大小比例（建议范围 0.5~2.0，1.0 为默认）
     * @param rotX       在正面基准上叠加的 X 轴旋转角度（度，俯仰，正值向下倾斜）
     * @param rotY       在正面基准上叠加的 Y 轴旋转角度（度，左右旋转，正值向右转）
     * @param rotZ       在正面基准上叠加的 Z 轴旋转角度（度，滚转，正值顺时针）
     * @return PNG 字节数组
     */
    public static byte[] renderPlayerToPng(int width, int height, float entitySize,
                                           float rotX, float rotY, float rotZ) {
        Minecraft mc = Minecraft.getInstance();

        if (!mc.isSameThread()) {
            return CompletableFuture.supplyAsync(
                    () -> renderPlayerToPng(width, height, entitySize, rotX, rotY, rotZ), mc
            ).join();
        }

        return doRender(mc, width, height, entitySize, rotX, rotY, rotZ);
    }

    private static byte[] doRender(Minecraft mc, int width, int height, float entitySize,
                                   float rotX, float rotY, float rotZ) {
        LocalPlayer player = mc.player;
        if (player == null) return new byte[0];

        // ── 临时覆盖玩家朝向，强制正面基准 ──────────────────────────────────
        // 保存原始旋转值
        float savedYRot        = player.getYRot();
        float savedYRotO       = player.yRotO;
        float savedXRot        = player.getXRot();
        float savedXRotO       = player.xRotO;
        float savedYHeadRot    = player.getYHeadRot();
        float savedYHeadRotO   = player.yHeadRotO;
        float savedYBodyRot    = player.yBodyRot;
        float savedYBodyRotO   = player.yBodyRotO;

        // 正面基准：yRot=0° 使模型面向摄像机，xRot=0° 水平
        // EntityRenderDispatcher.render 内部用 entityYRot 参数控制身体/头部朝向
        // 同时需要同步覆盖 player 字段，避免动画/头部旋转采样到游戏值
        player.setYRot(0f);
        player.yRotO       = 0f;
        player.setXRot(0f);
        player.xRotO       = 0f;
        player.setYHeadRot(0f);
        player.yHeadRotO   = 0f;
        player.yBodyRot    = 0f;
        player.yBodyRotO   = 0f;
        // ────────────────────────────────────────────────────────────────────

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

        // ── PoseStack 变换 ───────────────────────────────────────────────────
        // 基础缩放：以图像高度的一半作为 1 单位，再乘以 entitySize 自定义大小
        float baseScale = height * 0.5f * entitySize;

        PoseStack poseStack = new PoseStack();
        // 垂直居中偏移（将模型脚底大致对齐到画面下方 1/3 处）
        poseStack.translate(0, -height * 0.5f, -100f);
        poseStack.scale(baseScale, baseScale, baseScale);

        // 叠加自定义旋转（均以正面为基准，rotX/rotY/rotZ=0 即标准正面）
        poseStack.mulPose(Axis.XP.rotationDegrees(5f + rotX));  // 5° 微仰角让脸部可见
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotZ));
        // ────────────────────────────────────────────────────────────────────

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        boolean renderShadow = dispatcher.shouldRenderShadow;
        dispatcher.setRenderShadow(false);

        dispatcher.render(
                player,
                0d, 0d, 0d,
                0f,   // entityYRot=0：与字段覆盖一致，正面朝向摄像机
                1f,
                poseStack,
                bufferSource,
                LightTexture.FULL_BRIGHT
        );

        bufferSource.endBatch();
        dispatcher.setRenderShadow(renderShadow);

        // ── 还原玩家朝向 ─────────────────────────────────────────────────────
        player.setYRot(savedYRot);
        player.yRotO       = savedYRotO;
        player.setXRot(savedXRot);
        player.xRotO       = savedXRotO;
        player.setYHeadRot(savedYHeadRot);
        player.yHeadRotO   = savedYHeadRotO;
        player.yBodyRot    = savedYBodyRot;
        player.yBodyRotO   = savedYBodyRotO;
        // ────────────────────────────────────────────────────────────────────

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 4);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);

        fbo.unbindWrite();
        mc.getMainRenderTarget().bindWrite(true);
        fbo.destroyBuffers();

        flipVertically(pixelBuffer, width, height);

        return encodePng(pixelBuffer, width, height);
    }

    private static void flipVertically(ByteBuffer buffer, int width, int height) {
        int rowBytes = width * 4;
        byte[] top    = new byte[rowBytes];
        byte[] bottom = new byte[rowBytes];

        for (int y = 0; y < height / 2; y++) {
            int topPos    = y * rowBytes;
            int bottomPos = (height - 1 - y) * rowBytes;

            buffer.position(topPos);    buffer.get(top);
            buffer.position(bottomPos); buffer.get(bottom);
            buffer.position(topPos);    buffer.put(bottom);
            buffer.position(bottomPos); buffer.put(top);
        }
        buffer.rewind();
    }

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
                int r = raw[i]     & 0xFF;
                int g = raw[i + 1] & 0xFF;
                int b = raw[i + 2] & 0xFF;
                int a = raw[i + 3] & 0xFF;
                image.setPixelRGBA(x, y, (a << 24) | (b << 16) | (g << 8) | r);
            }
        }

        try {
            return image.asByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode player PNG", e);
        } finally {
            image.close();
        }
    }
}