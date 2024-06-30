package me.deathrealms.minesexpanded.command;

import me.deathrealms.minesexpanded.MinesExpanded;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.description.Description;

import java.util.List;

public class MinesCommand {

    private static final List<MECommand> COMMANDS = List.of(
            new HelpCommand(),
            new CreateCommand(),
            new ReloadCommand()
    );

    public MinesCommand(MinesExpanded plugin, BukkitCommandManager<CommandSender> manager) {
        manager.command(
                manager.commandBuilder("mines")
                        .commandDescription(Description.of("View all the mines."))
                        .handler(context -> {
                            context.sender().sendMessage("View all the mines.");
                        })
        );

        COMMANDS.forEach(command -> command.registerCommand(plugin, manager));
    }
}
