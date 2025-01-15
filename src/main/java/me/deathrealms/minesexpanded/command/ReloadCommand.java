package me.deathrealms.minesexpanded.command;

import me.deathrealms.minesexpanded.Mine;
import me.deathrealms.minesexpanded.MineRegistry;
import me.deathrealms.minesexpanded.MineServices;
import me.deathrealms.minesexpanded.MinesExpanded;
import me.deathrealms.minesexpanded.util.Message;
import me.deathrealms.minesexpanded.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.description.Description;

import java.util.List;

public class ReloadCommand implements MECommand {

    @Override
    public void registerCommand(MinesExpanded plugin, BukkitCommandManager<CommandSender> manager) {
        manager.command(
                manager.commandBuilder("mines")
                        .literal("reload")
                        .commandDescription(Description.of("Reload the plugin config and messages."))
                        .handler(context -> {
                            plugin.reloadConfig();
                            MessageUtil.loadMessages(plugin);

                            MineRegistry registry = plugin.mineRegistry();
                            MineServices services = plugin.mineServices();

                            List<Mine> mines = registry.getMines().values().stream().toList();

                            for (Mine mine : mines) {
                                services.removeService(mine);
                            }

                            registry.registerMines();
                            services.registerServices();

                            MessageUtil.message(context.sender(), Message.RELOAD_SUCCESS);
                        })
        );
    }
}
