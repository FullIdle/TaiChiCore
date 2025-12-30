package org.figsq.taichicore.taichicore;

import com.cinemamod.mcef.MCEFBrowser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import org.figsq.taichicore.taichicore.comm.FabricCommManager;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.*;

public class TaiChiCore implements ModInitializer {
    public static List<GuiConfig> guiConfigs = new ArrayList<>();
    public static MCEFBrowser browser;
    @Override
    public void onInitialize() {
        FabricCommManager.INSTANCE.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("taichidemo")
                .executes(context -> {
                    Minecraft.getInstance().execute(()-> Minecraft.getInstance().setScreen(new TaiChiScreen(null,null)));
                    return 1;
                })));
    }
}
