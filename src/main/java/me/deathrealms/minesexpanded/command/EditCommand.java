package me.deathrealms.minesexpanded.command;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import me.deathrealms.minesexpanded.Mine;
import me.deathrealms.minesexpanded.MineFile;
import me.deathrealms.minesexpanded.MinesExpanded;
import me.deathrealms.minesexpanded.util.MessageUtil;
import me.deathrealms.minesexpanded.util.SignMenu;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;

public class EditCommand implements MECommand {

    @Override
    public void registerCommand(MinesExpanded plugin, BukkitCommandManager<CommandSender> manager) {
        manager.command(
                manager.commandBuilder("mines")
                        .literal("edit")
                        .commandDescription(Description.of("Edit a mine."))
                        .permission("minesexpanded.command.edit")
                        .senderType(Player.class)
                        .required("name", StringParser.stringParser())
                        .handler(context -> {
                            Player player = context.sender();
                            String name = context.get("name");

                            if (!plugin.mineRegistry().hasMine(name)) {
                                player.sendMessage("That mine does not exist.");
                                return;
                            }

                            Mine mine = plugin.mineRegistry().getMine(name);
                            Gui mainMenu = Gui.gui()
                                    .title(MessageUtil.component("Editing: " + name))
                                    .rows(5)
                                    .create();

                            mainMenu.setDefaultClickAction(event -> event.setCancelled(true));

                            mainMenu.setItem(2, 3, ItemBuilder.from(Material.DIAMOND_PICKAXE).name(MessageUtil.component("&6Reset Time"))
                                    .lore(MessageUtil.component("&7Set the reset time of the mine.")).asGuiItem(event -> {
                                        SignMenu.open(player, new String[]{"", "^^^", "Enter reset time", "in seconds."}, lines -> {
                                            try {
                                                int time = Integer.parseInt(lines[0]);
                                                mine.setResetTime(time);
                                                MineFile file = mine.getFile();
                                                file.save();
                                                player.sendMessage("Reset time set to " + time + " seconds.");
                                            } catch (NumberFormatException e) {
                                                player.sendMessage("Invalid number.");
                                            }
                                        });
                                    }));

                            mainMenu.setItem(2, 5, ItemBuilder.from(Material.DIAMOND).name(MessageUtil.component("&6Reset Percentage"))
                                    .lore(MessageUtil.component("&7Set the reset percentage of the mine.")).asGuiItem(event -> {
                                        SignMenu.open(player, new String[]{"", "^^^", "Enter new", "reset percentage."}, lines -> {
                                            try {
                                                int percentage = Integer.parseInt(lines[0]);
                                                mine.setResetPercentage(percentage);
                                                MineFile file = mine.getFile();
                                                file.save();
                                                player.sendMessage("Reset percentage set to " + percentage + "%.");
                                            } catch (NumberFormatException e) {
                                                player.sendMessage("Invalid number.");
                                            }
                                        });
                                    }));

                            mainMenu.setItem(2, 7, ItemBuilder.from(Material.ENDER_PEARL).name(MessageUtil.component("&6Teleport Location"))
                                    .lore(MessageUtil.component("&7Set the teleport location of the mine.")).asGuiItem(event -> {
                                        mine.setTeleport(player.getLocation());
                                        mine.getFile().save();
                                        player.sendMessage("Teleport location set to your current location.");
                                    }));

                            mainMenu.setItem(4, 3, ItemBuilder.from(Material.STONE).name(MessageUtil.component("&6Blocks"))
                                    .lore(MessageUtil.component("&7Set the blocks of the mine.")).asGuiItem(event -> {
                                        player.sendMessage("Coming soon.");
                                    }));


                            mainMenu.setItem(5, 1, ItemBuilder.from(Material.ARROW).name(MessageUtil.component("&6Back")).asGuiItem(event -> player.performCommand("mines")));

                            mainMenu.open(player);
                        })
        );
    }
}
