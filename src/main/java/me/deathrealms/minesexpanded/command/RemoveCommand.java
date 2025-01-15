package me.deathrealms.minesexpanded.command;

import me.deathrealms.minesexpanded.Mine;
import me.deathrealms.minesexpanded.MineRegistry;
import me.deathrealms.minesexpanded.MineService;
import me.deathrealms.minesexpanded.MinesExpanded;
import me.deathrealms.minesexpanded.util.Message;
import me.deathrealms.minesexpanded.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;

public class RemoveCommand implements MECommand {

    @Override
    public void registerCommand(MinesExpanded plugin, BukkitCommandManager<CommandSender> manager) {
        manager.command(
                manager.commandBuilder("mines")
                        .literal("remove")
                        .commandDescription(Description.of("Remove a mine."))
                        .required("name", StringParser.stringParser())
                        .handler(context -> {
                            CommandSender sender = context.sender();
                            String name = context.get("name");

                            MineRegistry registry = plugin.mineRegistry();
                            if (!registry.hasMine(name)) {
                                MessageUtil.message(sender, Message.REMOVE_NO_MINE, name);
                                return;
                            }

                            Mine mine = registry.getMine(name);
                            MineService service = plugin.mineServices().getService(mine);
                            service.stop();
                            registry.removeMine(name);
                            mine.getFile().delete();
                            MessageUtil.message(sender, Message.REMOVE_SUCCESS, name);
                        })
        );
    }
}
