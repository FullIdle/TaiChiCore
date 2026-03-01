package org.figsq.taichicore.taichicore.cef;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.CefBrowserSettings;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsr;
import org.cef.browser.CefRequestContext;
import org.cef.callback.CefDragData;
import org.figsq.taichicore.taichicore.glfwtoawt.GlfwToAwtCursor;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * 渲染部分来源于 MCEF
 */
@Getter
public class TaiChiCefBrowser extends CefBrowserOsr {
    private final TaiChiCefRenderer renderer;

    protected ByteBuffer popupGraphics;
    private Rectangle popupSize;
    protected boolean showPopup = false;
    protected boolean popupDrawn = false;

    private int lastWidth;
    private int lastHeight;

    //拖拽
    private CefDragData currentDragData;
    private int currentDragMask;
    private int currentDragOperation;
    private boolean isDragging = false;

    public TaiChiCefBrowser(CefClient client,
                            String url,
                            boolean transparent,
                            CefRequestContext context,
                            CefBrowserOsr parent,
                            Point inspectAt,
                            CefBrowserSettings settings
    ) {
        super(
                client,
                url,
                transparent,
                context,
                parent,
                inspectAt,
                settings
        );

        TaiChiCefUtil.addBrowser(this);
        renderer = new TaiChiCefRenderer(transparent);
        Minecraft.getInstance().execute(renderer::initialize);
    }

    @Override
    public synchronized void onBeforeClose() {
        super.onBeforeClose();
        TaiChiCefUtil.removeBrowser(this);
    }

    @Override
    public void setFocus(boolean enable) {
        Minecraft.getInstance().execute(() -> super.setFocus(enable));
    }

    @Override
    public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height) {
        // nothing to update
        if (dirtyRects.length == 0)
            return;

        if (!popup) {
            if (lastWidth != width || lastHeight != height) {
                lastWidth = width;
                lastHeight = height;
                renderer.onPaint(buffer, width, height);
            } else {
                if (renderer.getTextureID() == 0) return;
                RenderSystem.bindTexture(renderer.getTextureID());
                RenderSystem.pixelStore(GL_UNPACK_ROW_LENGTH, width);
                for (Rectangle dirtyRect : dirtyRects) {
                    GlStateManager._pixelStore(GL_UNPACK_SKIP_PIXELS, dirtyRect.x);
                    GlStateManager._pixelStore(GL_UNPACK_SKIP_ROWS, dirtyRect.y);
                    renderer.onPaint(buffer, dirtyRect.x, dirtyRect.y, dirtyRect.width, dirtyRect.height);
                }
                if ((popupDrawn || showPopup) && popupSize != null) if (!showPopup) {
                    GlStateManager._pixelStore(GL_UNPACK_SKIP_PIXELS, popupSize.width);
                    GlStateManager._pixelStore(GL_UNPACK_SKIP_ROWS, popupSize.height);
                    renderer.onPaint(buffer, popupSize.x, popupSize.y, popupSize.width, popupSize.height);
                    popupGraphics = null;
                    popupSize = null;
                } else if (popupDrawn) {
                    RenderSystem.pixelStore(GL_UNPACK_ROW_LENGTH, popupSize.width);
                    GlStateManager._pixelStore(GL_UNPACK_SKIP_PIXELS, 0);
                    GlStateManager._pixelStore(GL_UNPACK_SKIP_ROWS, 0);
                    renderer.onPaint(popupGraphics, popupSize.x, popupSize.y, popupSize.width, popupSize.height);
                }
            }
        } else {
            if (renderer.getTextureID() == 0) return;
            RenderSystem.bindTexture(renderer.getTextureID());
            int start = buffer.capacity();
            int end = 0;
            for (Rectangle dirtyRect : dirtyRects) {
                RenderSystem.pixelStore(GL_UNPACK_ROW_LENGTH, popupSize.width);
                GlStateManager._pixelStore(GL_UNPACK_SKIP_PIXELS, dirtyRect.x);
                GlStateManager._pixelStore(GL_UNPACK_SKIP_ROWS, dirtyRect.y);
                renderer.onPaint(buffer, popupSize.x + dirtyRect.x, popupSize.y + dirtyRect.y, dirtyRect.width, dirtyRect.height);

                int rectStart = (dirtyRect.x + ((dirtyRect.y) * popupSize.width)) << 2;
                if (rectStart < start) start = rectStart;

                int rectEnd = ((dirtyRect.x + dirtyRect.width) + ((dirtyRect.y + popupSize.height) * dirtyRect.width)) << 2;
                if (rectEnd > end) end = rectEnd;
            }
            if (start < 0) start = 0;
            if (end > buffer.capacity()) end = buffer.capacity();

            if (end > start) if (this.popupGraphics != null) {
                long addrFrom = MemoryUtil.memAddress(buffer);
                long addrTo = MemoryUtil.memAddress(popupGraphics);
                MemoryUtil.memCopy(
                        addrFrom + start,
                        addrTo + start,
                        (end - start)
                );
            }

            popupDrawn = true;
        }
    }

    @Override
    public void close(boolean force) {
        this.cancelDrag();
        Minecraft.getInstance().execute(renderer::cleanup);
        super.close(force);
    }

    @Override
    protected void finalize() throws Throwable {
        Minecraft.getInstance().execute(renderer::cleanup);
        super.finalize();
    }

    public void resize(int width, int height) {
        width = scaleX(width);
        height = scaleY(height);

        browser_rect_.setBounds(0, 0, width, height);
        wasResized(width, height);
    }

    public int scaleX(int x) {
        return (int) (x / getDeviceScaleFactor());
    }

    public int scaleY(int y) {
        return (int) (y / getDeviceScaleFactor());
    }

    public double getDeviceScaleFactor() {
        long window = Minecraft.getInstance().getWindow().getWindow();

        int[] fbWidth = new int[1];
        int[] fbHeight = new int[1];
        GLFW.glfwGetFramebufferSize(window, fbWidth, fbHeight);

        int[] winWidth = new int[1];
        int[] winHeight = new int[1];
        GLFW.glfwGetWindowSize(window, winWidth, winHeight);

        return Math.max(1, Math.min(fbWidth[0] / winWidth[0], fbHeight[0] / winHeight[0]));
    }

    //拖拽逻辑
    @Override
    public boolean startDragging(CefBrowser browser, CefDragData dragData, int mask, int x, int y) {
        currentDragData = dragData;
        currentDragMask = mask;
        isDragging = true;
        dragTargetDragEnter(dragData, new Point(x, y), 0, mask);
        return true;
    }

    @Override
    public void updateDragCursor(CefBrowser browser, int operation) {
        super.updateDragCursor(browser, operation);
        currentDragOperation = operation;
    }

    public void onDragOver(int x, int y, int modifiers) {
        if (isDragging) dragTargetDragOver(new Point(x, y), modifiers, currentDragMask);
    }

    public void onDragDrop(int x, int y, int modifiers) {
        if (isDragging) {
            dragTargetDragOver(new Point(x, y), modifiers, currentDragMask);
            dragTargetDrop(new Point(x, y), modifiers);
            dragSourceEndedAt(new Point(x, y), currentDragOperation);
            dragSourceSystemDragEnded();
            isDragging = false;
            currentDragData = null;
        }
    }

    public void cancelDrag() {
        if (isDragging) {
            dragTargetDragLeave();
            dragSourceEndedAt(new Point(0, 0), 0);
            dragSourceSystemDragEnded();
            isDragging = false;
            currentDragData = null;
        }
    }

    //光标变化
    @Override
    public boolean onCursorChange(CefBrowser browser, int cursorType) {
        val window = Minecraft.getInstance().getWindow().getWindow();
        GLFW.glfwSetCursor(window, GlfwToAwtCursor.glfwCursor(cursorType));
        return true;
    }
}
