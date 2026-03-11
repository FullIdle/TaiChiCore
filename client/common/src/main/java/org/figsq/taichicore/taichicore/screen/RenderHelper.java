package org.figsq.taichicore.taichicore.screen;

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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.figsq.taichicore.taichicore.cef.handler.query.RenderNoticeHandler;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.mojang.blaze3d.systems.RenderSystem.glBindBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL21.*;

//一坨玩意，不过已经尽力优化了用空一举换掉
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

    private static byte[] ensureRenderThread(Supplier<byte[]> task) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isSameThread() && !RenderSystem.isOnRenderThread()) {
            return CompletableFuture.supplyAsync(task, mc).join();
        }
        return task.get();
    }

    // ── 原有静态方法，不动 ────────────────────────────────────────────────

    public static byte[] renderEntity(int entityId, int width, int height, float entitySize, boolean png, boolean followMouse) {
        return ensureRenderThread(() -> doRenderEntity(entityId, width, height, entitySize, png, followMouse));
    }

    public static byte[] renderPlayer(int width, int height, float entitySize, boolean png, boolean followMouse) {
        return renderEntity(-1, width, height, entitySize, png, followMouse);
    }

    private static Entity hitEntity(Minecraft mc) {
        val rs = mc.hitResult;
        if (rs == null) return null;
        if (rs.getType() != HitResult.Type.ENTITY) return null;
        return ((EntityHitResult) rs).getEntity();
    }

    private static byte[] doRenderEntity(int entityId, int width, int height, float entitySize, boolean png, boolean followMouse) {
        Minecraft mc = Minecraft.getInstance();
        val level = mc.level;
        if (level == null) return new byte[0];
        val entity = entityId == -1 ? mc.player : entityId == -999 ? hitEntity(mc) : level.getEntity(entityId);
        if (!(entity instanceof LivingEntity)) return new byte[0];
        val lent = ((LivingEntity) entity);

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

        float savedYBodyRot  = lent.yBodyRot;
        float savedYRot      = lent.getYRot();
        float savedXRot      = lent.getXRot();
        float savedYHeadRot  = lent.yHeadRot;
        float savedYHeadRotO = lent.yHeadRotO;

        lent.yBodyRot = 180f + p * 20f;
        lent.setYRot(  180f + p * 40f);
        lent.setXRot(       -q * 20f);
        lent.yHeadRot  = lent.getYRot();
        lent.yHeadRotO = lent.getYRot();

        float w = lent.getScale();
        Vector3f offset = new Vector3f(0f, lent.getBbHeight() / 2.0f * w, 0f);

        InventoryScreen.renderEntityInInventory(
                guiGraphics,
                centerX, centerY,
                (float) scale / w,
                offset,
                quaternionf,
                quaternionf2,
                lent
        );

        bufferSource.endBatch();

        lent.yBodyRot  = savedYBodyRot;
        lent.setYRot(savedYRot);
        lent.setXRot(savedXRot);
        lent.yHeadRot  = savedYHeadRot;
        lent.yHeadRotO = savedYHeadRotO;

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

    // ── 带 ctx 的公共重载 ─────────────────────────────────────────────────

    public static byte[] renderEntity(RenderNoticeHandler.NoticeContext ctx, int entityId, int width, int height, float entitySize, boolean png, boolean followMouse) {
        return ensureRenderThread(() -> doRenderEntity(ctx, entityId, width, height, entitySize, png, followMouse));
    }

    public static byte[] renderPlayer(RenderNoticeHandler.NoticeContext ctx, int width, int height, float entitySize, boolean png, boolean followMouse) {
        return renderEntity(ctx, -1, width, height, entitySize, png, followMouse);
    }

    public static byte[] renderInventorySlot(RenderNoticeHandler.NoticeContext ctx, int slot, int size, boolean png) {
        return ensureRenderThread(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return new byte[0];
            ItemStack stack = mc.player.getInventory().getItem(slot);
            return doRenderItem(ctx, stack, size, png);
        });
    }

    // ── 带 ctx 的私有实现 ─────────────────────────────────────────────────

    private static byte[] doRenderEntity(RenderNoticeHandler.NoticeContext ctx, int entityId, int width, int height, float entitySize, boolean png, boolean followMouse) {
        Minecraft mc = Minecraft.getInstance();
        val level = mc.level;
        if (level == null) return new byte[0];
        val entity = entityId == -1 ? mc.player : entityId == -999 ? hitEntity(mc) : level.getEntity(entityId);
        if (!(entity instanceof LivingEntity)) return new byte[0];
        val lent = ((LivingEntity) entity);

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

        if (ctx.fbo == null) ctx.fbo = new TextureTarget(width, height, true, Minecraft.ON_OSX);
        RenderTarget fbo = ctx.fbo;

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

        float savedYBodyRot  = lent.yBodyRot;
        float savedYRot      = lent.getYRot();
        float savedXRot      = lent.getXRot();
        float savedYHeadRot  = lent.yHeadRot;
        float savedYHeadRotO = lent.yHeadRotO;

        lent.yBodyRot = 180f + p * 20f;
        lent.setYRot(  180f + p * 40f);
        lent.setXRot(       -q * 20f);
        lent.yHeadRot  = lent.getYRot();
        lent.yHeadRotO = lent.getYRot();

        float w = lent.getScale();
        Vector3f offset = new Vector3f(0f, lent.getBbHeight() / 2.0f * w, 0f);

        InventoryScreen.renderEntityInInventory(
                guiGraphics,
                centerX, centerY,
                (float) scale / w,
                offset,
                quaternionf,
                quaternionf2,
                lent
        );

        bufferSource.endBatch();

        lent.yBodyRot  = savedYBodyRot;
        lent.setYRot(savedYRot);
        lent.setXRot(savedXRot);
        lent.yHeadRot  = savedYHeadRot;
        lent.yHeadRotO = savedYHeadRotO;

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        ByteBuffer mapped = readPixelsAsync(ctx, width, height);
        fbo.unbindWrite();
        mc.getMainRenderTarget().bindWrite(true);

        if (mapped == null) {
            // 第一帧回退同步，只发生一次
            fbo.bindRead();
            ByteBuffer sync = ByteBuffer.allocateDirect(width * height * 4);
            glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, sync);
            fbo.unbindRead();
            flipVertically(sync, width, height);
            return encode(sync, width, height, png);
        }
        ByteBuffer copy = ByteBuffer.allocateDirect(mapped.capacity());
        copy.put(mapped).rewind();
        unmapPBO(ctx);
        flipVertically(copy, width, height);
        return encode(copy, width, height, png);
    }

    private static byte[] doRenderItem(RenderNoticeHandler.NoticeContext ctx, ItemStack itemStack, int size, boolean png) {
        Minecraft mc = Minecraft.getInstance();
        if (itemStack == null || itemStack.isEmpty()) return new byte[0];

        final int GUI_SIZE = 16;

        if (ctx.fbo == null) ctx.fbo = new TextureTarget(size, size, true, Minecraft.ON_OSX);
        RenderTarget fbo = ctx.fbo;

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

        ByteBuffer mapped = readPixelsAsync(ctx, size, size);

        fbo.unbindWrite();
        mc.getMainRenderTarget().bindWrite(true);

        if (mapped == null) {
            // 第一帧回退同步，只发生一次
            fbo.bindRead();
            ByteBuffer sync = ByteBuffer.allocateDirect(size * size * 4);
            glReadPixels(0, 0, size, size, GL_RGBA, GL_UNSIGNED_BYTE, sync);
            fbo.unbindRead();
            flipVertically(sync, size, size);
            return encode(sync, size, size, png);
        }
        ByteBuffer copy = ByteBuffer.allocateDirect(mapped.capacity());
        copy.put(mapped).rewind();
        unmapPBO(ctx);
        flipVertically(copy, size, size);
        return encode(copy, size, size, png);
    }

    // ── PBO 辅助 ──────────────────────────────────────────────────────────

    private static void ensurePBOs(RenderNoticeHandler.NoticeContext ctx, int byteSize) {
        if (ctx.pboIds != null) return;
        ctx.pboIds = new int[2];
        glGenBuffers(ctx.pboIds);
        for (int id : ctx.pboIds) {
            glBindBuffer(GL_PIXEL_PACK_BUFFER, id);
            glBufferData(GL_PIXEL_PACK_BUFFER, (long) byteSize, GL_STREAM_READ);
        }
        glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
    }

    private static ByteBuffer readPixelsAsync(RenderNoticeHandler.NoticeContext ctx, int width, int height) {
        ensurePBOs(ctx, width * height * 4);

        // 本帧：发起 GPU→PBO 传输（异步，不阻塞）
        glBindBuffer(GL_PIXEL_PACK_BUFFER, ctx.pboIds[0]);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, 0L);

        // 读上帧已完成的 PBO
        glBindBuffer(GL_PIXEL_PACK_BUFFER, ctx.pboIds[1]);
        ByteBuffer mapped = glMapBuffer(GL_PIXEL_PACK_BUFFER, GL_READ_ONLY);

        // 交换双缓冲
        int tmp = ctx.pboIds[0]; ctx.pboIds[0] = ctx.pboIds[1]; ctx.pboIds[1] = tmp;

        glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
        return mapped; // 第一帧为 null
    }

    private static void unmapPBO(RenderNoticeHandler.NoticeContext ctx) {
        // 交换后 pboIds[0] 是刚读完的那个
        glBindBuffer(GL_PIXEL_PACK_BUFFER, ctx.pboIds[0]);
        glUnmapBuffer(GL_PIXEL_PACK_BUFFER);
        glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
    }
}