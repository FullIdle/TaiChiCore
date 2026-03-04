package org.figsq.taichicore.taichicore;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.val;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.CommandSourceStack;
import org.figsq.taichicore.taichicore.comm.ModCommManager;
import org.figsq.taichicore.taichicore.screen.TaiChiScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import java.util.function.Consumer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public abstract class TaiChiCore {
    public static final LiteralArgumentBuilder<CommandSourceStack> CMD =
            literal("taichicore")
                    .then(literal("url").then(argument("url", StringArgumentType.greedyString()).executes(context -> {
                        val url = StringArgumentType.getString(context, "url");
                        val minecraft = Minecraft.getInstance();
                        minecraft.execute(() -> minecraft.setScreen(new TaiChiScreen(url)));
                        return 1;
                    })))
                    .then(literal("huburl").then(argument("huburl", StringArgumentType.greedyString()).executes(context -> {
                        val url = StringArgumentType.getString(context, "huburl");
                        TaiChiCore.HUB.getBrowser().loadURL(url);
                        return 1;
                    })));
    public static TaiChiCore INSTANCE;
    public static final String MOD_ID = "taichicore";
    public static final String MOD_NAME = "TaiChiCore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final TaiChiScreen HUB = new TaiChiScreen("C:\\Users\\COLORFUL\\Downloads\\test.html");

    public TaiChiCore() {
        INSTANCE = this;
    }

    public void init() {
        initComm();
    }

    public void initComm() {
        ModCommManager.INSTANCE.init();
        initPlatformComm();
    }

    public void renderHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        val minecraft = Minecraft.getInstance();
        val newWidth = minecraft.getWindow().getGuiScaledWidth();
        val newHeight = minecraft.getWindow().getGuiScaledHeight();
        if (HUB.width != newWidth || HUB.height != newHeight)
            if (HUB.getBrowser() == null) HUB.init(minecraft, newWidth, newHeight);
            else HUB.resize(minecraft, newWidth, newHeight);
        HUB.render(guiGraphics, HUB.width / 2, HUB.height / 2, deltaTracker.getGameTimeDeltaPartialTick(true));
    }

    public abstract void initPlatformComm();

    public abstract void sendToServer(byte[] bytes);

    public abstract ScriptEngine getScriptEngine();

    public Object evalScript(String script) {
        return evalScript(script, null);
    }

    public Object evalScript(String script, Consumer<ScriptContext> extras) {
        val context = addTaiChiAttributes(new SimpleScriptContext());
        if (extras != null) extras.accept(context);
        try {
            return getScriptEngine().eval(script, context);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加一些方便用的属性
     * 可惜的是写成脚本还得考虑remap
     * neoforge除外
     */
    public <T extends ScriptContext> T addTaiChiAttributes(T context) {
        val scope = ScriptContext.ENGINE_SCOPE;
        context.setAttribute("minecraft", Minecraft.getInstance(), scope);
        context.setAttribute("player", Minecraft.getInstance().player, scope);
        return context;
    }
}
