package org.figsq.taichicore.taichicore.command;

import lombok.val;
import me.fullidle.ficore.ficore.common.api.commands.CommandBuilder;
import me.fullidle.ficore.ficore.common.api.inventory.InvButton;
import me.fullidle.ficore.ficore.common.api.inventory.InvConfig;
import me.fullidle.ficore.ficore.common.api.inventory.InvHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

import static me.fullidle.ficore.ficore.common.api.commands.CommandBuilder.builder;

public class DebugCommand {
    public static final CommandBuilder BUILDER = builder("test")
            .exec(context -> {
                val builder = new InvConfig.Builder("DEBUG GUI");
                val config = builder.layout(Collections.singletonList("0        "))
                        .button('0', new InvButton.Builder()
                                .display(new ItemStack(Material.STONE))
                                .actions(ClickType.LEFT, (inventoryClickEvent, invButton, invTransformer) -> {
                                    System.out.println("left");
                                })
                                .actions(ClickType.RIGHT, (a, b, c) -> {
                                    System.out.println("right");
                                })
                                .build())
                        .build();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    val inv = new InvHolder(config, player).getInventory();
                    player.openInventory(inv);
                }
            });
}
