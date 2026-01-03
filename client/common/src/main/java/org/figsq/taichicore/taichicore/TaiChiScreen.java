package org.figsq.taichicore.taichicore;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.figsq.taichicore.taichicore.cef.TaiChiLoadHandlerAdapter;
import org.figsq.taichicore.taichicore.cef.queries.DisplaySlotHandler;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * @see com.cinemamod.mcef.example.ExampleScreen
 */
public class TaiChiScreen extends Screen {
    public ContainerScreen parent;
    public Map<Integer, DisplaySlotHandler.DisplayArgs> displaySlots = new HashMap<>();
    public String path;
    public boolean renderCursorStack = true;
    private MCEFBrowser browser;

    public TaiChiScreen(@Nullable String path, @Nullable ContainerScreen parent) {
        super(Component.literal("taichidemo"));
        val absolutePath = Minecraft.getInstance().getResourcePackDirectory().toFile().getAbsolutePath();
        this.path = (path == null || path.isEmpty() ? "mod://taichicore/main.html" : path).replace("{resource}", absolutePath);
        this.parent = parent;
    }

    protected void init() {
        super.init();
        if (this.browser == null) {
            this.browser = MCEF.createBrowser(path, true);
            this.resizeBrowser();
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
        if (this.width > 100 && this.height > 100)
            this.browser.resize(this.scaleX(this.width), this.scaleY(this.height));
    }

    public void resize(Minecraft minecraft, int i, int j) {
        super.resize(minecraft, i, j);
        this.resizeBrowser();
    }

    public void onClose() {
        if (this.parent != null) this.parent.onClose();
        TaiChiLoadHandlerAdapter.waitOrLoading = true;
        this.browser.close(true);
        super.onClose();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
/*
        super.renderBackground(guiGraphics, i, j, f);
*/
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float f) {
        super.render(guiGraphics, mouseX, mouseY, f);

        if (TaiChiLoadHandlerAdapter.waitOrLoading) return;
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

        if (this.parent != null) {
            val handler = this.parent.getMenu();
            for (Map.Entry<Integer, DisplaySlotHandler.DisplayArgs> entry : this.displaySlots.entrySet()) {
                val index = entry.getKey();
                val args = entry.getValue();
                val slot = handler.getSlot(index);
                val stack = slot.getItem();
                if (stack == null || stack.isEmpty()) continue;
                val count = stack.getCount();
                this.drawItem(guiGraphics, stack, args.x, args.y, count == 1 ? "" : count + "", args.scaleX, args.scaleY, args.depth);
            }

            if (this.renderCursorStack) {
                val cursorStack = handler.getCarried();
                if (cursorStack != null && !cursorStack.isEmpty()) {
                    val count = cursorStack.getCount();
                    this.drawItem(guiGraphics, cursorStack, mouseX, mouseY, count == 1 ? "" : count + "", 1.0f, 1.0f, 1.1f);
                }
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.browser.sendMousePress(this.mouseX(mouseX), this.mouseY(mouseY), button);
        this.browser.setFocus(true);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * @see ContainerScreen
     */
    public void drawItem(GuiGraphics guiGraphics, ItemStack itemStack, int x, int y, String amountText, float scaleX, float scaleY, float depth) {
        x = (int) (x - (8 * scaleX));
        y = (int) (y - (8 * scaleY));
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 232.0F);
        guiGraphics.pose().scale(scaleX, scaleY, depth);
        guiGraphics.renderItem(itemStack, (int) (x / scaleX), (int) (y / scaleY));
        guiGraphics.renderItemDecorations(this.font, itemStack, (int) (x / scaleX), (int) (y / scaleY), amountText);
        guiGraphics.pose().popPose();
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.browser.sendMouseRelease(this.mouseX(mouseX), this.mouseY(mouseY), button);
        this.browser.setFocus(true);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void mouseMoved(double mouseX, double mouseY) {
        this.browser.sendMouseMove(this.mouseX(mouseX), this.mouseY(mouseY));
        super.mouseMoved(mouseX, mouseY);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.browser.sendMouseWheel(this.mouseX(mouseX), this.mouseY(mouseY), scrollY, 0);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.browser.sendKeyPress(keyCode, scanCode, modifiers);
        this.browser.setFocus(true);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.browser.sendKeyRelease(keyCode, scanCode, modifiers);
        this.browser.setFocus(true);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (codePoint == 0) {
            return false;
        } else {
            this.browser.sendKeyTyped(codePoint, modifiers);
            this.browser.setFocus(true);
            return super.charTyped(codePoint, modifiers);
        }
    }
}
