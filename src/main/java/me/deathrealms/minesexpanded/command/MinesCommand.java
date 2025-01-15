package me.deathrealms.minesexpanded.command;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.deathrealms.minesexpanded.MinesExpanded;
import me.deathrealms.minesexpanded.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.description.Description;

import java.util.ArrayList;
import java.util.List;

public class MinesCommand {

    private static final List<MECommand> COMMANDS = List.of(
            new HelpCommand(),
            new CreateCommand(),
            new RemoveCommand(),
            new ReloadCommand(),
            new EditCommand()
    );

    public MinesCommand(MinesExpanded plugin, BukkitCommandManager<CommandSender> manager) {
        manager.command(
                manager.commandBuilder("mines")
                        .commandDescription(Description.of("View all the mines."))
                        .senderType(Player.class)
                        .handler(context -> {
                            Player player = context.sender();

                            PaginatedGui gui = Gui.paginated()
                                    .title(MessageUtil.component("Mines"))
                                    .rows(6)
                                    .disableAllInteractions()
                                    .create();

                            gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).name(MessageUtil.component("&cPrevious")).asGuiItem(event -> gui.previous()));
                            gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).name(MessageUtil.component("&aNext")).asGuiItem(event -> gui.next()));

                            for (String mine : plugin.mineRegistry().getMines().keySet()) {
                                List<Component> lore = new ArrayList<>();
                                lore.add(MessageUtil.component(""));
                                lore.add(MessageUtil.component("&6Left-Click &7to teleport to mine."));
                                if (player.isOp()) {
                                    lore.add(MessageUtil.component("&6Right-Click &7to edit mine."));
                                }

                                GuiItem item = ItemBuilder.from(Material.STONE)
                                        .name(MessageUtil.component("&6" + mine + " &7Mine"))
                                        .lore(lore)
                                        .asGuiItem(event -> {
                                            if (event.isRightClick()) {
                                                player.performCommand("mines edit " + mine);
                                                return;
                                            }
                                            player.sendMessage("Teleporting to " + mine + " mine");
                                            player.teleport(plugin.mineRegistry().getMine(mine).getTeleport());
                                        });

                                gui.addItem(item);
                            }

                            gui.open(player);
                        })
        );

        COMMANDS.forEach(command -> command.registerCommand(plugin, manager));
    }
}
