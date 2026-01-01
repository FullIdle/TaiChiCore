package org.figsq.taichicore.taichicore;

import com.google.common.cache.AbstractCache;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.MCEFApi;
import org.figsq.taichicore.taichicore.cef.queries.DisplaySlotHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import scala.Int;

import java.util.Map;

public class TaiChiScreen extends GuiScreen {
    public GuiContainer parent;
    public Map<Integer, DisplaySlotHandler.DisplayArgs> displaySlots;
    IBrowser browser;
    private String urlToLoad;

    public TaiChiScreen(String url) {
        this.urlToLoad = url == null ? "mod://taichicore/main.html" : url;
    }

    public void initGui() {
        if (this.browser == null) {
            this.browser = MCEFApi.getAPI().createBrowser(this.urlToLoad == null ? MCEF.HOME_PAGE : this.urlToLoad, true);
            this.urlToLoad = null;
        }

        if (this.browser != null) {
            this.browser.resize(this.mc.displayWidth, this.mc.displayHeight);
        }

        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
    }

    public void updateScreen() {
        if (this.urlToLoad != null && this.browser != null) {
            this.browser.loadURL(this.urlToLoad);
            this.urlToLoad = null;
        }

    }

    public void drawScreen(int i1, int i2, float f) {
        super.drawScreen(i1, i2, f);
        if (this.browser != null) {
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();

            GlStateManager.blendFunc(
                    GL11.GL_SRC_ALPHA,
                    GL11.GL_ONE_MINUS_SRC_ALPHA
            );
            GlStateManager.tryBlendFuncSeparate(
                    GL11.GL_SRC_ALPHA,
                    GL11.GL_ONE_MINUS_SRC_ALPHA,
                    GL11.GL_ONE,
                    GL11.GL_ZERO
            );
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.browser.draw(0.0F, this.height, this.width, 0);
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
        }

    }

    public void onGuiClosed() {
        if (this.browser != null) this.browser.close();

        Keyboard.enableRepeatEvents(false);
    }

    public void handleInput() {
        while(Keyboard.next()) {
            if (Keyboard.getEventKey() == 1) {
                this.mc.displayGuiScreen(null);
                return;
            }

            boolean pressed = Keyboard.getEventKeyState();
            char key = Keyboard.getEventCharacter();
            int num = Keyboard.getEventKey();
            if (this.browser != null) {
                if (pressed) {
                    this.browser.injectKeyPressedByKeyCode(num, key, 0);
                } else {
                    this.browser.injectKeyReleasedByKeyCode(num, key, 0);
                }

                if (key != 0) {
                    this.browser.injectKeyTyped(key, 0);
                }
            }
        }

        while(Mouse.next()) {
            int btn = Mouse.getEventButton();
            boolean pressed = Mouse.getEventButtonState();
            int sx = Mouse.getEventX();
            int sy = Mouse.getEventY();
            int wheel = Mouse.getEventDWheel();
            if (this.browser != null) {
                int y = this.mc.displayHeight - sy;
                if (wheel != 0) {
                    this.browser.injectMouseWheel(sx, y, 0, 1, wheel);
                } else if (btn == -1) {
                    this.browser.injectMouseMove(sx, y, 0, y < 0);
                } else {
                    this.browser.injectMouseButton(sx, y, 0, btn + 1, pressed, 1);
                }
            }

            if (pressed) {
                int x = sx * this.width / this.mc.displayWidth;
                int y = this.height - sy * this.height / this.mc.displayHeight - 1;

                try {
                    this.mouseClicked(x, y, btn);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

    }
}
