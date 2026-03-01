package org.figsq.taichicore.taichicore;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.figsq.taichicore.taichicore.cef.TaiChiCefBrowser;
import org.figsq.taichicore.taichicore.cef.TaiChiCefUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class TaiChiScreen extends Screen {
    private static final double BROWSER_DRAW_OFFSET = 20;
    private TaiChiCefBrowser browser;
    private final String url;
    private int btnMask = 0;

    public TaiChiScreen(String url) {
        super(Component.literal("TaiChi"));
        this.url = url;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.browser != null) {
            this.browser.close(true);
            this.browser = null;
        }
    }

    @Override
    protected void init() {
        super.init();
        if (this.browser == null) {
            this.browser = TaiChiCefUtil.createBrowser(url, true);
            resizeBrowser();
            browser.setFocus(true);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
//        super.renderBackground(guiGraphics, i, j, f);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        RenderSystem.disableDepthTest();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ZERO
        );

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, this.browser.getRenderer().getTextureID());
        Tesselator t = Tesselator.getInstance();
        BufferBuilder buffer = t.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex((float) BROWSER_DRAW_OFFSET, (float) (height - BROWSER_DRAW_OFFSET), 0).setUv(0.0f, 1.0f).setColor(255, 255, 255, 255);
        buffer.addVertex((float) (width - BROWSER_DRAW_OFFSET), (float) (height - BROWSER_DRAW_OFFSET), 0).setUv(1.0f, 1.0f).setColor(255, 255, 255, 255);
        buffer.addVertex((float) (width - BROWSER_DRAW_OFFSET), (float) BROWSER_DRAW_OFFSET, 0).setUv(1.0f, 0.0f).setColor(255, 255, 255, 255);
        buffer.addVertex((float) BROWSER_DRAW_OFFSET, (float) BROWSER_DRAW_OFFSET, 0).setUv(0.0f, 0.0f).setColor(255, 255, 255, 255);
        BufferUploader.drawWithShader(buffer.build());
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private int mouseX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET) * minecraft.getWindow().getGuiScale());
    }

    private int mouseY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET) * minecraft.getWindow().getGuiScale());
    }

    private int scaleX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET * 2) * minecraft.getWindow().getGuiScale());
    }

    private int scaleY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET * 2) * minecraft.getWindow().getGuiScale());
    }

    private void resizeBrowser() {
        if (width > 100 && height > 100) {
            browser.resize(scaleX(width), scaleY(height));
        }
    }

    @Override
    public void resize(Minecraft minecraft, int i, int j) {
        super.resize(minecraft, i, j);
        resizeBrowser();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static int getMouseEventButton(int mcButton) {
        return switch (mcButton) {
            case 0 -> MouseEvent.BUTTON1;
            case 1 -> MouseEvent.BUTTON3;
            case 2 -> MouseEvent.BUTTON2;
            default -> MouseEvent.NOBUTTON;
        };
    }

    public static int getButtonMask(int mcButton) {
        return switch (mcButton) {
            case 0 -> InputEvent.BUTTON1_DOWN_MASK;
            case 1 -> InputEvent.BUTTON3_DOWN_MASK;
            case 2 -> InputEvent.BUTTON2_DOWN_MASK;
            default -> 0;
        };
    }

    private int glfwModsToAwt(int mods) {
        int result = 0;
        if ((mods & GLFW.GLFW_MOD_SHIFT) != 0)    result |= InputEvent.SHIFT_DOWN_MASK;
        if ((mods & GLFW.GLFW_MOD_CONTROL) != 0)  result |= InputEvent.CTRL_DOWN_MASK;
        if ((mods & GLFW.GLFW_MOD_ALT) != 0)      result |= InputEvent.ALT_DOWN_MASK;
        if ((mods & GLFW.GLFW_MOD_SUPER) != 0)    result |= InputEvent.META_DOWN_MASK;
        return result;
    }

    private int glfwKeyToAwt(int glfwKey) {
        return switch (glfwKey) {
            case GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_ENTER -> KeyEvent.VK_ENTER;
            case GLFW.GLFW_KEY_BACKSPACE      -> KeyEvent.VK_BACK_SPACE;
            case GLFW.GLFW_KEY_TAB            -> KeyEvent.VK_TAB;
            case GLFW.GLFW_KEY_ESCAPE         -> KeyEvent.VK_ESCAPE;
            case GLFW.GLFW_KEY_DELETE         -> KeyEvent.VK_DELETE;
            case GLFW.GLFW_KEY_LEFT           -> KeyEvent.VK_LEFT;
            case GLFW.GLFW_KEY_RIGHT          -> KeyEvent.VK_RIGHT;
            case GLFW.GLFW_KEY_UP             -> KeyEvent.VK_UP;
            case GLFW.GLFW_KEY_DOWN           -> KeyEvent.VK_DOWN;
            case GLFW.GLFW_KEY_HOME           -> KeyEvent.VK_HOME;
            case GLFW.GLFW_KEY_END            -> KeyEvent.VK_END;
            case GLFW.GLFW_KEY_PAGE_UP        -> KeyEvent.VK_PAGE_UP;
            case GLFW.GLFW_KEY_PAGE_DOWN      -> KeyEvent.VK_PAGE_DOWN;
            case GLFW.GLFW_KEY_F1             -> KeyEvent.VK_F1;
            case GLFW.GLFW_KEY_F2             -> KeyEvent.VK_F2;
            case GLFW.GLFW_KEY_F3             -> KeyEvent.VK_F3;
            case GLFW.GLFW_KEY_F4             -> KeyEvent.VK_F4;
            case GLFW.GLFW_KEY_F5             -> KeyEvent.VK_F5;
            case GLFW.GLFW_KEY_F6             -> KeyEvent.VK_F6;
            case GLFW.GLFW_KEY_F7             -> KeyEvent.VK_F7;
            case GLFW.GLFW_KEY_F8             -> KeyEvent.VK_F8;
            case GLFW.GLFW_KEY_F9             -> KeyEvent.VK_F9;
            case GLFW.GLFW_KEY_F10            -> KeyEvent.VK_F10;
            case GLFW.GLFW_KEY_F11            -> KeyEvent.VK_F11;
            case GLFW.GLFW_KEY_F12            -> KeyEvent.VK_F12;
            case GLFW.GLFW_KEY_LEFT_SHIFT,
                 GLFW.GLFW_KEY_RIGHT_SHIFT    -> KeyEvent.VK_SHIFT;
            case GLFW.GLFW_KEY_LEFT_CONTROL,
                 GLFW.GLFW_KEY_RIGHT_CONTROL  -> KeyEvent.VK_CONTROL;
            case GLFW.GLFW_KEY_LEFT_ALT,
                 GLFW.GLFW_KEY_RIGHT_ALT      -> KeyEvent.VK_ALT;
            default                           -> glfwKey;
        };
    }

    private void setScanCode(KeyEvent event, long scanCode) {
        try {
            java.lang.reflect.Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);

            java.lang.reflect.Field scancodeField = KeyEvent.class.getDeclaredField("scancode");
            long offset = unsafe.objectFieldOffset(scancodeField);
            unsafe.putLong(event, offset, scanCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        btnMask |= getButtonMask(button);
        browser.sendMouseEvent(new MouseEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                btnMask,
                mouseX(mouseX), mouseY(mouseY),
                1, false, getMouseEventButton(button)
        ));
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        browser.onDragDrop(mouseX(mouseX), mouseY(mouseY), btnMask);
        browser.sendMouseEvent(new MouseEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                MouseEvent.MOUSE_RELEASED,
                System.currentTimeMillis(),
                btnMask,
                mouseX(mouseX), mouseY(mouseY),
                0, false, getMouseEventButton(button)
        ));
        btnMask &= ~getButtonMask(button);
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        browser.sendMouseEvent(new MouseEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(),
                btnMask,
                mouseX(mouseX), mouseY(mouseY),
                0, false, MouseEvent.NOBUTTON
        ));
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        browser.onDragOver(mouseX(mouseX), mouseY(mouseY), btnMask);
        browser.sendMouseEvent(new MouseEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                MouseEvent.MOUSE_DRAGGED,
                System.currentTimeMillis(),
                btnMask,
                mouseX(mouseX), mouseY(mouseY),
                0, false, getMouseEventButton(button)
        ));
        super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        double amount = scrollY > 0 ? Math.ceil(scrollY) : Math.floor(scrollY);
        amount *= BROWSER_DRAW_OFFSET;
        browser.sendMouseWheelEvent(new MouseWheelEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                MouseEvent.MOUSE_WHEEL,
                System.currentTimeMillis(),
                btnMask,
                mouseX(mouseX), mouseY(mouseY),
                0, false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL,
                3,
                (int) amount
        ));
        super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        val e = new KeyEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                glfwModsToAwt(modifiers),
                glfwKeyToAwt(keyCode),
                KeyEvent.CHAR_UNDEFINED
        );
        setScanCode(e, scanCode & 0xFF);
        browser.sendKeyEvent(e);
        super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        val e = new KeyEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                glfwModsToAwt(modifiers),
                glfwKeyToAwt(keyCode),
                KeyEvent.CHAR_UNDEFINED
        );
        setScanCode(e, scanCode & 0xFF);
        browser.sendKeyEvent(e);
        super.keyReleased(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (codePoint == (char) 0) return false;
        browser.sendKeyEvent(new KeyEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                KeyEvent.KEY_TYPED,
                System.currentTimeMillis(),
                glfwModsToAwt(modifiers),
                KeyEvent.VK_UNDEFINED,
                codePoint
        ));
        super.charTyped(codePoint, modifiers);
        return true;
    }
}