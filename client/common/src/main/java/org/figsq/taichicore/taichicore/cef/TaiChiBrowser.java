/*
 *     MCEF (Minecraft Chromium Embedded Framework)
 *     Copyright (C) 2023 CinemaMod Group
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package org.figsq.taichicore.taichicore.cef;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsr;
import org.cef.callback.CefDragData;
import org.cef.handler.CefScreenInfo;
import org.figsq.taichicore.taichicore.cef.misc.CefCursorType;
import org.figsq.taichicore.taichicore.cef.misc.CefPlatform;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.nio.ByteBuffer;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * An instance of an "Off-screen rendered" Chromium web browser.
 * Complete with a renderer, keyboard and mouse inputs, optional
 * browser control shortcuts, cursor handling, drag & drop support.
 */
public class TaiChiBrowser extends CefBrowserOsr {
    /**
     * The renderer for the browser.
     */
    @Getter
    private final TaiChiRenderer renderer;
    /**
     * Stores information about drag & drop.
     */
    private final TaiChiDragContext dragContext = new TaiChiDragContext();

    private boolean browserControls = true;
    /**
     * Used to track when a full repaint should occur.
     */
    private int lastWidth = 0, lastHeight = 0;
    /**
     * A bitset representing what mouse buttons are currently pressed.
     * CEF is a bit odd and implements mouse buttons as a part of modifier flags.
     */
    private int btnMask = 0;
    /**
     * Whether MCEF should automatically calculate the device scale factor.
     * The device scale factor is used to determine how content should be scaled based on the
     * DPI of the monitor that the window is on. This is important for high-DPI displays where
     * the device scale factor is greater than 1.
     */
    @Getter
    @Setter
    private boolean autoDSF;
    @Setter
    private double deviceScaleFactor = 1;

    // Data relating to popups and graphics
    // Marked as protected in-case a mod wants to extend MCEFBrowser and override the repaint logic
    protected ByteBuffer popupGraphics;
    protected Rectangle popupSize;
    protected boolean showPopup = false;
    protected boolean popupDrawn = false;

    public TaiChiBrowser(CefClient client, String url, boolean transparent) {
        this(client, url, transparent, false);
    }

    public TaiChiBrowser(CefClient client, String url, boolean transparent, boolean autoDSF) {
        super(client, url, transparent, null, null);
        renderer = new TaiChiRenderer(transparent);
        this.autoDSF = autoDSF;

        Minecraft.getInstance().submit(renderer::initialize);
    }

    public double getDeviceScaleFactor() {
        if (autoDSF) {
            long window = Minecraft.getInstance().getWindow().getWindow();

            int[] fbWidth = new int[1];
            int[] fbHeight = new int[1];
            glfwGetFramebufferSize(window, fbWidth, fbHeight);

            int[] winWidth = new int[1];
            int[] winHeight = new int[1];
            glfwGetWindowSize(window, winWidth, winHeight);

            // The device scale factor is the ratio of the allocated framebuffer size to the window size
            // https://stackoverflow.com/questions/44719635/what-is-the-difference-between-glfwgetwindowsize-and-glfwgetframebuffersize
            return Math.max(1, Math.min(fbWidth[0] / winWidth[0], fbHeight[0] / winHeight[0]));
        } else {
            return deviceScaleFactor > 0 ? deviceScaleFactor : 1;
        }
    }

    public int scaleX(int x) {
        return (int) (x / getDeviceScaleFactor());
    }

    public int scaleY(int y) {
        return (int) (y / getDeviceScaleFactor());
    }

    public boolean getScreenInfo(CefBrowser browser, CefScreenInfo screenInfo) {
        super.getScreenInfo(browser, screenInfo);
        screenInfo.device_scale_factor = getDeviceScaleFactor();
        return true;
    }

    public boolean usingBrowserControls() {
        return browserControls;
    }

    /**
     * Enabling browser controls tells MCEF to mimic the behavior of an actual browser.
     * CTRL+R for reload, CTRL+Left for back, CTRL+Right for forward, etc...
     *
     * @param browserControls whether browser controls should be enabled
     * @return the browser instance
     */
    public TaiChiBrowser useBrowserControls(boolean browserControls) {
        this.browserControls = browserControls;
        return this;
    }

    public TaiChiDragContext getDragContext() {
        return dragContext;
    }

    // Popups
    @Override
    public void onPopupShow(CefBrowser browser, boolean show) {
        super.onPopupShow(browser, show);
        showPopup = show;
        if (!show) popupDrawn = false;
    }

    @Override
    public void onPopupSize(CefBrowser browser, Rectangle size) {
        super.onPopupSize(browser, size);
        popupSize = size;
        this.popupGraphics = ByteBuffer.allocateDirect(
                size.width * size.height * 4
        );
    }

    // Graphics
    @Override
    public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height) {
        // nothing to update
        if (dirtyRects.length == 0)
            return;

        if (!popup) {
            if (lastWidth != width || lastHeight != height) {
                lastWidth = width;
                lastHeight = height;
                // upload full texture
                // this also sets up the texture size and creates the texture
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
                if ((popupDrawn || showPopup) && popupSize != null) {
                    // interpret where the popup was as a dirty rect
                    if (!showPopup) {
                        // if the popup is not visible, just draw the contents of the buffer
                        GlStateManager._pixelStore(GL_UNPACK_SKIP_PIXELS, popupSize.width);
                        GlStateManager._pixelStore(GL_UNPACK_SKIP_ROWS, popupSize.height);
                        renderer.onPaint(buffer, popupSize.x, popupSize.y, popupSize.width, popupSize.height);
                        popupGraphics = null;
                        popupSize = null;
                    } else if (popupDrawn) {
                        // else, a use copy of the popup graphics, as it needs to remain visible
                        // and for some reason that I do not for the life of me understand, chromium does not seem to keep this data in memory outside of the paint loop, meaning it has to be copied around, which wastes performance
                        RenderSystem.pixelStore(GL_UNPACK_ROW_LENGTH, popupSize.width);
                        GlStateManager._pixelStore(GL_UNPACK_SKIP_PIXELS, 0);
                        GlStateManager._pixelStore(GL_UNPACK_SKIP_ROWS, 0);
                        renderer.onPaint(popupGraphics, popupSize.x, popupSize.y, popupSize.width, popupSize.height);
                    }
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

            if (end > start) {
                // TODO: check if it's more performant to go for row-wise copies or if it's better to just copy the updated region
                if (this.popupGraphics != null) {
                    long addrFrom = MemoryUtil.memAddress(buffer);
                    long addrTo = MemoryUtil.memAddress(popupGraphics);
                    MemoryUtil.memCopy(
                            addrFrom + start,
                            addrTo + start,
                            (end - start)
                    );
                }
            }

            popupDrawn = true;
        }
    }

    public void resize(int width, int height) {
        width = scaleX(width);
        height = scaleY(height);

        browser_rect_.setBounds(0, 0, width, height);
        wasResized(width, height);
    }

    // Inputs
    public void sendKeyPress(int keyCode, long scanCode, int modifiers) {
        int inputs = 0;
        if (browserControls) {
            if (modifiers == GLFW_MOD_CONTROL) {
                if (keyCode == GLFW_KEY_R) {
                    reload();
                    return;
                } else if (keyCode == GLFW_KEY_EQUAL) {
                    if (getZoomLevel() < 9) setZoomLevel(getZoomLevel() + 1);
                    return;
                } else if (keyCode == GLFW_KEY_MINUS) {
                    if (getZoomLevel() > -9) setZoomLevel(getZoomLevel() - 1);
                    return;
                } else if (keyCode == GLFW_KEY_0) {
                    setZoomLevel(0);
                    return;
                }
                inputs = InputEvent.CTRL_DOWN_MASK;
            } else if (modifiers == GLFW_MOD_ALT) {
                if (keyCode == GLFW_KEY_LEFT && canGoBack()) {
                    goBack();
                    return;
                } else if (keyCode == GLFW_KEY_RIGHT && canGoForward()) {
                    goForward();
                    return;
                }
                inputs = inputs | InputEvent.ALT_DOWN_MASK;
            }
        }

        KeyEvent e = new KeyEvent(
                new Component() {
                    @Override
                    public String getName() {
                        return "TaiChiCore";
                    }
                },
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                inputs,
                keyCode,
                (char) keyCode
        );
        sendKeyEvent(e);
    }

    public void sendKeyRelease(int keyCode, long scanCode, int modifiers) {
        if (browserControls) {
            if (modifiers == GLFW_MOD_CONTROL) {
                if (keyCode == GLFW_KEY_R) return;
                else if (keyCode == GLFW_KEY_EQUAL) return;
                else if (keyCode == GLFW_KEY_MINUS) return;
                else if (keyCode == GLFW_KEY_0) return;
            } else if (modifiers == GLFW_MOD_ALT) {
                if (keyCode == GLFW_KEY_LEFT && canGoBack()) return;
                else if (keyCode == GLFW_KEY_RIGHT && canGoForward()) return;
            }
        }

        KeyEvent e = new KeyEvent(
                new Component() {
                    @Override
                    public String getName() {
                        return "TaiChiCore";
                    }
                },
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                modifiers,
                keyCode,
                (char) keyCode
        );
        sendKeyEvent(e);
    }

    public void sendKeyTyped(char c, int modifiers) {
        if (browserControls) {
            if (modifiers == GLFW_MOD_CONTROL) {
                if ((int) c == GLFW_KEY_R) return;
                else if ((int) c == GLFW_KEY_EQUAL) return;
                else if ((int) c == GLFW_KEY_MINUS) return;
                else if ((int) c == GLFW_KEY_0) return;
            } else if (modifiers == GLFW_MOD_ALT) {
                if ((int) c == GLFW_KEY_LEFT && canGoBack()) return;
                else if ((int) c == GLFW_KEY_RIGHT && canGoForward()) return;
            }
        }
        KeyEvent e = new KeyEvent(
                new Component() {
                    @Override
                    public String getName() {
                        return "TaiChiCore";
                    }
                },
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                modifiers,
                c,
                c
        );
        sendKeyEvent(e);
    }

    public void sendMouseMove(int mouseX, int mouseY) {
        mouseX = scaleX(mouseX);
        mouseY = scaleY(mouseY);

        MouseEvent e = new MouseEvent(
                new Component() {
                    @Override
                    public String getName() {
                        return "TaiChiCore";
                    }
                },
                MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(),
                dragContext.getVirtualModifiers(btnMask),
                mouseX,
                mouseY,
                0,
                false
        );
        sendMouseEvent(e);
        if (dragContext.isDragging())
            this.dragTargetDragOver(new Point(mouseX, mouseY), 0, dragContext.getMask());
    }

    public void sendMousePress(int mouseX, int mouseY, int button) {
        mouseX = scaleX(mouseX);
        mouseY = scaleY(mouseY);

        if (button == 1) button = 2;
        else if (button == 2) button = 1;

        int btnMask = 0;
        if (button == 0) btnMask |= InputEvent.BUTTON1_DOWN_MASK;
        else if (button == 1) btnMask |= InputEvent.BUTTON2_DOWN_MASK;
        else if (button == 2) btnMask |= InputEvent.BUTTON3_DOWN_MASK;

        // 构造 AWT 原生 MouseEvent（鼠标按下事件）
        MouseEvent e = new MouseEvent(
                new Component() {
                    @Override
                    public String getName() {
                        return "TaiChiCore";
                    }
                },
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                btnMask,
                mouseX,
                mouseY,
                1,
                false
        );
        sendMouseEvent(e);
    }

    public void sendMouseRelease(int mouseX, int mouseY, int button) {
        mouseX = scaleX(mouseX);
        mouseY = scaleY(mouseY);

        // MC中中键/右键互换的逻辑保留
        if (button == 1) button = 2;
        else if (button == 2) button = 1;

        // 替换旧版 BUTTON_MASK 为 AWT InputEvent 常量
        if (button == 0 && (btnMask & InputEvent.BUTTON1_DOWN_MASK) != 0)
            btnMask ^= InputEvent.BUTTON1_DOWN_MASK;
        else if (button == 1 && (btnMask & InputEvent.BUTTON2_DOWN_MASK) != 0)
            btnMask ^= InputEvent.BUTTON2_DOWN_MASK;
        else if (button == 2 && (btnMask & InputEvent.BUTTON3_DOWN_MASK) != 0)
            btnMask ^= InputEvent.BUTTON3_DOWN_MASK;

        // 构造 AWT 原生 MouseEvent（鼠标释放事件）
        MouseEvent e = new MouseEvent(
                new Component() {
                    @Override
                    public String getName() {
                        return "TaiChiCore";
                    }
                },
                MouseEvent.MOUSE_RELEASED, // 对应 GLFW_RELEASE/鼠标释放
                System.currentTimeMillis(),
                btnMask,
                mouseX,
                mouseY,
                1, // 点击次数（释放事件填1）
                false
        );
        sendMouseEvent(e); // 新版入参为 AWT MouseEvent

        // 拖拽逻辑保留不变
        if (dragContext.isDragging()) {
            if (button == 0) {
                finishDragging(mouseX, mouseY);
            }
        }
    }

    public void sendMouseWheel(int mouseX, int mouseY, double amount, int modifiers) {
        mouseX = scaleX(mouseX);
        mouseY = scaleY(mouseY);

        if (browserControls) {
            if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) { // 替换 GLFW_MOD_CONTROL
                if (amount > 0) {
                    if (getZoomLevel() < 9) setZoomLevel(getZoomLevel() + 1);
                } else if (getZoomLevel() > -9) setZoomLevel(getZoomLevel() - 1);
                return;
            }
        }

        // macOS 滚动速度适配逻辑保留
        if (!CefPlatform.getPlatform().isMacOS()) {
            if (amount < 0) {
                amount = Math.floor(amount);
            } else {
                amount = Math.ceil(amount);
            }
            amount = amount * 3;
        }

        // 构造 AWT 原生 MouseWheelEvent（替换旧版 CefMouseWheelEvent）
        MouseWheelEvent e = new MouseWheelEvent(
                new Component() {
                    @Override
                    public String getName() {
                        return "TaiChiCore";
                    }
                },                                  // 事件源
                MouseWheelEvent.MOUSE_WHEEL,           // 滚轮事件类型
                System.currentTimeMillis(),            // 时间戳
                modifiers,                             // 修饰键
                mouseX,                                // 鼠标X坐标
                mouseY,                                // 鼠标Y坐标
                0,                                     // 点击次数（滚轮事件填0）
                false,                                 // 弹出菜单标记
                MouseWheelEvent.WHEEL_UNIT_SCROLL,     // 滚轮滚动单位（对应旧版 WHEEL_UNIT_SCROLL）
                1,                                     // 每个刻度的滚动量
                (int) amount                           // 滚轮滚动值（amount转为int）
        );
        sendMouseWheelEvent(e); // 新版入参为 AWT MouseWheelEvent
    }

    // Drag & drop
    @Override
    public boolean startDragging(CefBrowser browser, CefDragData dragData, int mask, int x, int y) {
        x = scaleX(x);
        y = scaleY(y);

        dragContext.startDragging(dragData, mask);
        this.dragTargetDragEnter(dragContext.getDragData(), new Point(x, y), btnMask, dragContext.getMask());
        return false;
    }

    @Override
    public void updateDragCursor(CefBrowser browser, int operation) {
        if (dragContext.updateCursor(operation))
            // If the cursor to display for the drag event changes, then update the cursor
            this.onCursorChange(this, dragContext.getVirtualCursor(dragContext.getActualCursor()));

        super.updateDragCursor(browser, operation);
    }

    public void startDragging(CefDragData dragData, int mask, int x, int y) { // Overload since the JCEF method requires a browser, which then goes unused
        x = scaleX(x);
        y = scaleY(y);

        startDragging(dragData, mask, x, y);
    }

    public void finishDragging(int x, int y) {
        x = scaleX(x);
        y = scaleY(y);

        dragTargetDrop(new Point(x, y), btnMask);
        dragTargetDragLeave();
        dragContext.stopDragging();
        this.onCursorChange(this, dragContext.getActualCursor());
    }

    public void cancelDrag() {
        dragTargetDragLeave();
        dragContext.stopDragging();
        this.onCursorChange(this, dragContext.getActualCursor());
    }

    public void close() {
        renderer.cleanup();
        setCursor(CefCursorType.fromId(0));
        super.close(true);
    }

    @Override
    protected void finalize() throws Throwable {
        Minecraft.getInstance().submit(renderer::cleanup);
        super.finalize();
    }

    // Cursor handling
    @Override
    public boolean onCursorChange(CefBrowser browser, int cursorType) {
        cursorType = dragContext.getVirtualCursor(cursorType);
        setCursor(CefCursorType.fromId(cursorType));
        return super.onCursorChange(browser, cursorType);
    }

    public void setCursor(CefCursorType cursorType) {
        if (cursorType == CefCursorType.NONE) {
            glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        } else {
            glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), getGLFWCursorHandle(cursorType));
        }
    }

    public static long getGLFWCursorHandle(CefCursorType cursorType) {
        if (CEF_TO_GLFW_CURSORS.containsKey(cursorType)) return CEF_TO_GLFW_CURSORS.get(cursorType);
        long glfwCursorHandle = glfwCreateStandardCursor(cursorType.glfwId);
        CEF_TO_GLFW_CURSORS.put(cursorType, glfwCursorHandle);
        return glfwCursorHandle;
    }

    private static final HashMap<CefCursorType, Long> CEF_TO_GLFW_CURSORS = new HashMap<>();
}
