package org.figsq.taichicore.taichicore.command;

import lombok.val;
import me.fullidle.ficore.ficore.common.api.commands.CommandBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.fullidle.ficore.ficore.common.api.commands.CommandBuilder.args;
import static me.fullidle.ficore.ficore.common.api.commands.CommandBuilder.builder;

public class DebugCommand {
    public static final CommandBuilder BUILDER = builder("test")
            .exec(context -> {
                if (context.sender instanceof Player) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    val inv = Bukkit.createInventory(null, 9, "自定义GUI1");
                    val values = Material.values();
                    for (int i = 0; i < 9; i++) inv.addItem(new ItemStack(values[i]));
                    player.openInventory(inv);
                }
            });
}
