package org.figsq.taichicore.taichicore.command;

import lombok.val;
import me.fullidle.ficore.ficore.common.api.commands.CommandBuilder;
import me.fullidle.ficore.ficore.common.api.commands.Context;
import me.fullidle.ficore.ficore.common.api.commands.args.Args;
import me.fullidle.ficore.ficore.common.api.commands.args.EnumArgs;
import me.fullidle.ficore.ficore.common.api.commands.args.player.MultiplayerArgs;
import me.fullidle.ficore.ficore.common.api.commands.args.types.StringArgs;
import org.bukkit.entity.Player;
import org.figsq.taichicore.taichicore.comm.PluginCommManager;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenUrlPacket;
import org.figsq.taichicore.taichicore.config.GuiConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.fullidle.ficore.ficore.common.api.commands.CommandBuilder.args;
import static me.fullidle.ficore.ficore.common.api.commands.CommandBuilder.builder;

public class OpenCommand {
    public static Args<GuiConfig> CONFIG_ARGS = new Args<GuiConfig>() {
        @Override
        public GuiConfig parse(Context context, String s) {
            return GuiConfigManager.configs.stream().filter(config -> config.identity.equals(s)).findFirst().orElse(null);
        }

        @Override
        public List<String> prompts() {
            return GuiConfigManager.configs.stream().map(c->c.identity).collect(Collectors.toList());
        }
    };

    public static final StringArgs STRING_ARGS = new StringArgs(new ArrayList<>());


    public static final CommandBuilder BUILDER = builder("open")
            .then(builder("gui").then(args("targets", MultiplayerArgs.INSTANCE).then(args("gui", CONFIG_ARGS).exec(context -> {
                val players = MultiplayerArgs.INSTANCE.get(context, "targets");
                val config = CONFIG_ARGS.get(context, "gui");
                val packet = new OpenUrlPacket(config.url);
                for (Player player : players) PluginCommManager.INSTANCE.sendTo(player, packet);
            }))))
            .then(builder("path").then(args("targets", MultiplayerArgs.INSTANCE).then(args("url", STRING_ARGS).exec(context -> {
                val players = MultiplayerArgs.INSTANCE.get(context, "targets");
                val url = STRING_ARGS.get(context, "url");
                val packet = new OpenUrlPacket(url);
                for (Player player : players) PluginCommManager.INSTANCE.sendTo(player, packet);
            }))));
}
