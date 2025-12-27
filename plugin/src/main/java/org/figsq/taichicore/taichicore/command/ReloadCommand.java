package org.figsq.taichicore.taichicore.command;

import me.fullidle.ficore.ficore.common.api.commands.CommandBuilder;
import org.figsq.taichicore.taichicore.TaiChiCore;

import static me.fullidle.ficore.ficore.common.api.commands.CommandBuilder.*;
import static org.figsq.taichicore.taichicore.command.Commands.permission;

public class ReloadCommand {
    public static final CommandBuilder BUILDER = builder("reload")
            .permission(permission("reload"))
            .exec(context -> {
                TaiChiCore.getInstance().reloadConfig();
                context.sender.sendMessage("§a配置已重新加载!");
            });
}
