package org.figsq.taichicore.taichicore;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.figsq.taichicore.taichicore.cef.TaiChiLoadHandlerAdapter;
import org.figsq.taichicore.taichicore.cef.queries.DisplaySlotHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TaiChiScreen extends Screen {
    private MCEFBrowser browser;

    public GenericContainerScreen parent;
    public Map<Integer, DisplaySlotHandler.DisplayArgs> displaySlots = new HashMap<>();
    public String path;

    public TaiChiScreen(@Nullable String path, @Nullable GenericContainerScreen parent) {
        super(Text.literal("taichidemo"));
        val absolutePath = MinecraftClient.getInstance().getResourcePackDir().toFile().getAbsolutePath();
        System.out.println(absolutePath);
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
        return (int) (x * this.client.getWindow().getScaleFactor());
    }

    private int mouseY(double y) {
        return (int) (y * this.client.getWindow().getScaleFactor());
    }

    private int scaleX(double x) {
        return (int) (x * this.client.getWindow().getScaleFactor());
    }

    private int scaleY(double y) {
        return (int) (y * this.client.getWindow().getScaleFactor());
    }

    private void resizeBrowser() {
        if (this.width > 100 && this.height > 100)
            this.browser.resize(this.scaleX(this.width), this.scaleY(this.height));
    }

    public void resize(MinecraftClient minecraft, int i, int j) {
        super.resize(minecraft, i, j);
        this.resizeBrowser();
    }

    public void close() {
        if (this.parent != null) this.parent.close();
        TaiChiLoadHandlerAdapter.waitOrLoading = true;
        this.browser.close(true);
        super.close();
    }

    public void render(DrawContext guiGraphics, int mouseX, int mouseY, float f) {
        super.render(guiGraphics, mouseX, mouseY, f);

        if (TaiChiLoadHandlerAdapter.waitOrLoading) return;
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, this.browser.getRenderer().getTextureID());
        Tessellator t = Tessellator.getInstance();
        BufferBuilder buffer = t.getBuffer();
        buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(0.0F, this.height, 0.0F).texture(0.0F, 1.0F).color(255, 255, 255, 255).next();
        buffer.vertex(this.width, this.height, 0.0F).texture(1.0F, 1.0F).color(255, 255, 255, 255).next();
        buffer.vertex(this.width, 0.0F, 0.0F).texture(1.0F, 0.0F).color(255, 255, 255, 255).next();
        buffer.vertex(0.0F, 0.0F, 0.0F).texture(0.0F, 0.0F).color(255, 255, 255, 255).next();
        t.draw();
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();

        if (this.parent != null) {
            val handler = this.parent.getScreenHandler();
            for (Map.Entry<Integer, DisplaySlotHandler.DisplayArgs> entry : this.displaySlots.entrySet()) {
                val index = entry.getKey();
                val args = entry.getValue();
                val slot = handler.getSlot(index);
                val stack = slot.getStack();
                if (stack == null || stack.isEmpty()) continue;
                val count = stack.getCount();
                this.drawItem(guiGraphics, stack, args.x, args.y, count == 1 ? "" : count + "", args.scaleX, args.scaleY, args.depth);
            }
            val cursorStack = handler.getCursorStack();
            if (cursorStack != null) {
                val count = cursorStack.getCount();
                this.drawItem(guiGraphics, cursorStack, mouseX, mouseY, count == 1 ? "" : count + "", 1.0f, 1.0f, 1.1f);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.browser.sendMousePress(this.mouseX(mouseX), this.mouseY(mouseY), button);
        this.browser.setFocus(true);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void drawItem(DrawContext context, ItemStack itemStack, int x, int y, String amountText, float scaleX, float scaleY, float depth) {
        x = (int) (x - (8 * scaleX));
        y = (int) (y - (8 * scaleY));
        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 0.0F, 232.0F);
        context.getMatrices().scale(scaleX, scaleY, depth);
        context.drawItem(itemStack, (int) (x / scaleX), (int) (y / scaleY));
        context.drawItemInSlot(this.textRenderer, itemStack, (int) (x / scaleX), (int) (y / scaleY), amountText);
        context.getMatrices().pop();
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

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.browser.sendMouseWheel(this.mouseX(mouseX), this.mouseY(mouseY), delta, 0);
        return super.mouseScrolled(mouseX, mouseY, delta);
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
