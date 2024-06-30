package me.deathrealms.minesexpanded.command;

import me.deathrealms.minesexpanded.MinesExpanded;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.BukkitCommandManager;

public interface MECommand {

    void registerCommand(MinesExpanded plugin, BukkitCommandManager<CommandSender> manager);
}
