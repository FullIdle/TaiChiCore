package org.figsq.taichicore.taichicore;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import org.figsq.taichicore.taichicore.comm.ModCommManager;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

public abstract class TaiChiCore {
    public static final LiteralArgumentBuilder<CommandSourceStack> CMD = literal("taichicore").executes(context -> {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new TaiChiScreen(null, null)));
        return 1;
    });
    public static List<GuiConfig> guiConfigs = new ArrayList<>();
    public static TaiChiCore INSTANCE;

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
