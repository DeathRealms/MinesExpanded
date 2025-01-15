package me.deathrealms.minesexpanded.command;

import me.deathrealms.minesexpanded.Mine;
import me.deathrealms.minesexpanded.MineRegistry;
import me.deathrealms.minesexpanded.MineServices;
import me.deathrealms.minesexpanded.MinesExpanded;
import me.deathrealms.minesexpanded.command.parser.MineParser;
import me.deathrealms.minesexpanded.util.Message;
import me.deathrealms.minesexpanded.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.description.Description;

public class RemoveCommand implements MECommand {

    @Override
    public void registerCommand(MinesExpanded plugin, BukkitCommandManager<CommandSender> manager) {
        manager.command(
                manager.commandBuilder("mines")
                        .literal("remove")
                        .commandDescription(Description.of("Remove a mine."))
                        .required("mine", MineParser.mineParser())
                        .handler(context -> {
                            CommandSender sender = context.sender();
                            Mine mine = context.get("mine");
                            String name = mine.getName();

                            MineRegistry registry = plugin.mineRegistry();
                            MineServices services = plugin.mineServices();

                            registry.removeMine(name);
                            services.removeService(mine);

                            MessageUtil.message(sender, Message.REMOVE_SUCCESS, name);
                        })
        );
    }
}
