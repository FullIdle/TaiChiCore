package org.figsq.taichicore.taichicore.command;

import lombok.val;
import me.fullidle.ficore.ficore.common.api.commands.args.player.MultiplayerArgs;
import org.bukkit.entity.HumanEntity;
import org.figsq.taichicore.taichicore.comm.PluginCommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.client.CloseHUDPacket;

import static me.fullidle.ficore.ficore.common.api.commands.CommandBuilder.args;
import static me.fullidle.ficore.ficore.common.api.commands.CommandBuilder.builder;

public class Commands {
    public static final String[] helps = {
            "§a§lTaiChiCore HELP",
            "§7§l/taichi reload  §f§l-重载配置",
            "§7§l/taichi open gui [targets] [gui]  §f§l-为玩家打开指定配置的gui",
            "§7§l/taichi open url [targets] [url]  §f§l-为玩家打开指定url",
    };

    public static void register() {
        builder("taichicore")
                .then(builder("help").exec(context -> context.sender.sendMessage(helps)))
                .then(ReloadCommand.BUILDER)
                .then(OpenCommand.BUILDER)
                .then(DebugCommand.BUILDER)
                .then(builder("close").then(args("targets", MultiplayerArgs.INSTANCE).exec(context -> {
                    val players = MultiplayerArgs.INSTANCE.get(context, "targets");
                    players.forEach(HumanEntity::closeInventory);
                })))
                .then(builder("closehud").then(args("targets", MultiplayerArgs.INSTANCE).exec(context -> {
                    val players = MultiplayerArgs.INSTANCE.get(context, "targets");
                    val instance = PluginCommManager.INSTANCE;
                    val encode = instance.encode(new CloseHUDPacket());
                    players.forEach(p-> instance.sendTo(p, encode));
                })))
                .registerPluginCommand();
    }

    public static String permission(String cmd) {
        return "taichicore.command." + cmd;
    }
}
