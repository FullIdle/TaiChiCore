package org.figsq.taichicore.taichicore;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import org.figsq.taichicore.taichicore.comm.ModCommManager;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public abstract class TaiChiCore {
    public static final LiteralArgumentBuilder<CommandSourceStack> CMD =
            literal("taichicore").then(argument("url", StringArgumentType.greedyString()).executes(context -> {
                val url = StringArgumentType.getString(context, "url");
                val minecraft = Minecraft.getInstance();
                minecraft.execute(() -> minecraft.setScreen(new TaiChiScreen(url)));
                return 1;
            }));
    public static List<GuiConfig> guiConfigs = new ArrayList<>();
    public static TaiChiCore INSTANCE;
    public static final String MOD_ID = "taichicore";
    public static final String MOD_NAME = "TaiChiCore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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

    public abstract void initPlatformComm();

    public abstract void sendToServer(byte[] bytes);
}
