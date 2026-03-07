package org.figsq.taichicore.taichicore;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.figsq.taichicore.taichicore.comm.PluginCommManager;
import org.figsq.taichicore.taichicore.command.Commands;
import org.figsq.taichicore.taichicore.config.CustomPacketHandlerManager;
import org.figsq.taichicore.taichicore.config.GuiConfigManager;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

public class TaiChiCore extends JavaPlugin {
    @Getter
    private static TaiChiCore instance;
    @Getter
    private static final NashornScriptEngine scriptEngine = (NashornScriptEngine) new NashornScriptEngineFactory().getScriptEngine(TaiChiCore.class.getClassLoader());

    public TaiChiCore() {
        instance = this;
    }

    @Override
    public void onEnable() {
        PluginCommManager.INSTANCE.init();
        Commands.register();
        BukkitListener.register();
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        this.saveDefaultConfig();
        super.reloadConfig();
        GuiConfigManager.load();
        CustomPacketHandlerManager.load();
        for (Player player : Bukkit.getOnlinePlayers()) TaiChiCoreAPI.INSTANCE.updateGuiConfig(player);
    }
}
