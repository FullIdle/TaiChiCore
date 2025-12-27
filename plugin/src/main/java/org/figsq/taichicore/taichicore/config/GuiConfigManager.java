package org.figsq.taichicore.taichicore.config;

import lombok.val;
import org.bukkit.configuration.file.YamlConfiguration;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiConfigManager {
    public static File folder = new File(TaiChiCore.getInstance().getDataFolder(), "gui");
    public static List<GuiConfig> configs = new ArrayList<>();
    public static void register() {
        configs.clear();
        if (!folder.exists()) {
            folder.mkdirs();
            val plugin = TaiChiCore.getInstance();
            plugin.saveResource("gui/example.yml", false);
        }
        val files = folder.listFiles();
        if (files != null) for (File file : files) {
            val config = new GuiConfig();
            val yaml = YamlConfiguration.loadConfiguration(file);
            config.identity = yaml.getString("identity");
            config.type = GuiConfig.Type.valueOf(yaml.getString("type").toUpperCase());
            config.match = yaml.getString("match");
            config.url = yaml.getString("url");
            configs.add(config);
        }
    }
}
