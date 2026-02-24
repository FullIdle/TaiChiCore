package org.figsq.taichicore.taichicore;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import lombok.val;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.figsq.taichicore.taichicore.comm.ModCommManager;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public abstract class TaiChiCore {
    public static final LiteralArgumentBuilder<CommandSourceStack> CMD = literal("taichicore").executes(context -> {
                Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new TaiChiScreen(null, null)));
                return 1;
            }).then(literal("testhud").executes(context -> {
                        Minecraft.getInstance().execute(() -> {
                            setHudScreen(new TaiChiScreen(null, null));
                        });
                        return 1;
                    })
                    .then(argument("url", StringArgumentType.string()).executes(context -> {
                        Minecraft.getInstance().execute(() -> {
                            val url = StringArgumentType.getString(context, "url");
                            setHudScreen(new TaiChiScreen(url, null));
                        });
                        return 1;
                    })))
            .then(literal("test").executes(context -> {
                val cefAppBuilder = new CefAppBuilder();
                try {
                    cefAppBuilder.build();
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("test"));
                } catch (IOException | UnsupportedPlatformException | InterruptedException | CefInitializationException e) {
                    throw new RuntimeException(e);
                }
                return 1;
            }));
    public static List<GuiConfig> guiConfigs = new ArrayList<>();
    public static TaiChiCore INSTANCE;
    @Getter
    private static TaiChiScreen hudScreen;
/*
    public static final NashornScriptEngine SCRIPT_ENGINE = (NashornScriptEngine) new NashornScriptEngineFactory().getScriptEngine(TaiChiCore.class.getClassLoader());
*/

    public static void setHudScreen(TaiChiScreen hudScreen) {
        closeHudScreen();
        TaiChiCore.hudScreen = hudScreen;
    }

    public static void closeHudScreen() {
        if (TaiChiCore.hudScreen != null) TaiChiCore.hudScreen.onClose();
    }

    public TaiChiCore() {
        INSTANCE = this;
    }

    public void init() {
        initComm();
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public void close() {
        closeHudScreen();
    }

    public void initComm() {
        ModCommManager.INSTANCE.init();
        initPlatformComm();
    }

    public void renderHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (hudScreen == null) return;
        val minecraft = Minecraft.getInstance();
        val newWidth = minecraft.getWindow().getGuiScaledWidth();
        val newHeight = minecraft.getWindow().getGuiScaledHeight();
        if (hudScreen.width != newWidth || hudScreen.height != newHeight)
            if (hudScreen.browser == null)
                hudScreen.init(minecraft, newWidth, newHeight);
            else
                hudScreen.resize(minecraft, newWidth, newHeight);
        hudScreen.render(guiGraphics, hudScreen.width / 2, hudScreen.height / 2, deltaTracker.getGameTimeDeltaPartialTick(true));
    }

    public abstract void initPlatformComm();

    public abstract void sendToServer(byte[] bytes);
}
