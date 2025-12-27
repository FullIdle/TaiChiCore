package org.figsq.taichicore.taichicore;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.val;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import org.figsq.taichicore.taichicore.cef.TaiChiLoadHandlerAdapter;
import org.figsq.taichicore.taichicore.comm.FabricCommManager;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.*;

public class TaiChiCore implements ModInitializer {
    public static List<GuiConfig> guiConfigs = new ArrayList<>();
    public static MCEFBrowser browser;
    @Override
    public void onInitialize() {
        FabricCommManager.INSTANCE.init();
        HudRenderCallback.EVENT.register((context,f)->{
            if (browser == null) browser = MCEF.createBrowser("D:\\IdeaProjects\\TaiChiCore\\1201\\run\\resourcepacks\\guiclicktest.html", true,1920, 1080);
            if (TaiChiLoadHandlerAdapter.waitOrLoading) return;
            val window = MinecraftClient.getInstance().getWindow();
            val height = window.getScaledHeight();
            val width = window.getScaledWidth();
            RenderSystem.disableDepthTest();
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            RenderSystem.setShaderTexture(0, browser.getRenderer().getTextureID());
            Tessellator t = Tessellator.getInstance();
            BufferBuilder buffer = t.getBuffer();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(0.0F, height, 0.0F).texture(0.0F, 1.0F).color(255, 255, 255, 255).next();
            buffer.vertex(width, height, 0.0F).texture(1.0F, 1.0F).color(255, 255, 255, 255).next();
            buffer.vertex(width, 0.0F, 0.0F).texture(1.0F, 0.0F).color(255, 255, 255, 255).next();
            buffer.vertex(0.0F, 0.0F, 0.0F).texture(0.0F, 0.0F).color(255, 255, 255, 255).next();
            t.draw();
            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.enableDepthTest();
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("taichidemo")
                .executes(context -> {
                    MinecraftClient.getInstance().execute(()-> MinecraftClient.getInstance().setScreen(new TaiChiScreen(null,null)));
                    return 1;
                })));
    }
}
