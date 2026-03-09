package org.figsq.taichicore.taichicore.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.Getter;
import lombok.val;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.figsq.taichicore.taichicore.cef.TaiChiCefBrowser;
import org.figsq.taichicore.taichicore.cef.TaiChiCefUtil;
import org.figsq.taichicore.taichicore.glfwtoawt.GlfwToAwtKey;
import org.lwjgl.opengl.GL11;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class TaiChiScreen extends Screen {
    @Getter
    private TaiChiCefBrowser browser;
    private final String url;
    private int btnMask = 0;

    public TaiChiScreen(String url) {
        super(Component.literal("TaiChiScreen"));
        this.url = TaiChiCefBrowser.formatURL(url);
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
        if (this.browser.isClosed())
            this.minecraft.player.sendSystemMessage(Component.literal("Opened the closed Screen in the browser").withStyle(ChatFormatting.RED));
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
        buffer.addVertex(0.0F, this.height, 0.0F).setUv(0.0F, 1.0F).setColor(255, 255, 255, 255);
        buffer.addVertex(this.width, this.height, 0.0F).setUv(1.0F, 1.0F).setColor(255, 255, 255, 255);
        buffer.addVertex(this.width, 0.0F, 0.0F).setUv(1.0F, 0.0F).setColor(255, 255, 255, 255);
        buffer.addVertex(0.0F, 0.0F, 0.0F).setUv(0.0F, 0.0F).setColor(255, 255, 255, 255);
        BufferUploader.drawWithShader(buffer.build());
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();


        //渲染拖拽的数据
        if (browser.isDragging() && browser.getCurrentDragData() != null) {
            val dragData = browser.getCurrentDragData();
            val text = dragData.isLink() ? (dragData.getLinkTitle().isEmpty() ? dragData.getLinkURL() : dragData.getLinkTitle())
                    : dragData.isFragment() ? dragData.getFragmentText()
                    : dragData.isFile() ? dragData.getFileName()
                    : null;
            if (text != null && !text.isEmpty()) if (text.length() > 8)
                guiGraphics.renderTooltip(font, Component.literal(text.substring(0, 8) + "..."), mouseX, mouseY);
            else
                guiGraphics.renderTooltip(font, Component.literal(text), mouseX, mouseY);
        }
    }

    private int mouseX(double x) {
        return (int) (x * this.minecraft.getWindow().getGuiScale());
    }

    private int mouseY(double y) {
        return (int) (y * this.minecraft.getWindow().getGuiScale());
    }

    private int scaleX(double x) {
        return (int) (x * this.minecraft.getWindow().getGuiScale());
    }

    private int scaleY(double y) {
        return (int) (y * this.minecraft.getWindow().getGuiScale());
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
        amount *= 20;
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
        browser.sendKeyEvent(GlfwToAwtKey.createKeyEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                keyCode,
                modifiers,
                scanCode,
                false
        ));
        super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        browser.sendKeyEvent(GlfwToAwtKey.createKeyEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                keyCode,
                modifiers,
                scanCode,
                true
        ));
        super.keyReleased(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (codePoint == (char) 0) return false;
        browser.sendKeyEvent(GlfwToAwtKey.createCharTypedEvent(
                TaiChiCefUtil.AWT_TAICHI_COMPONENT,
                codePoint
        ));
        super.charTyped(codePoint, modifiers);
        return true;
    }

    /**
     * 某些情况下用于判断是否初始化等
     */
    public Minecraft getMinecraft() {
        return this.minecraft;
    }
}