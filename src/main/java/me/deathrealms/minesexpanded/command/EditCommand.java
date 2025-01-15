package me.deathrealms.minesexpanded.command;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.deathrealms.minesexpanded.Mine;
import me.deathrealms.minesexpanded.MineFile;
import me.deathrealms.minesexpanded.MinesExpanded;
import me.deathrealms.minesexpanded.util.Message;
import me.deathrealms.minesexpanded.util.MessageUtil;
import me.deathrealms.minesexpanded.util.SignMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
                                MessageUtil.message(player, Message.EDIT_NO_MINE, name);
                                return;
                            }

                            openMainGui(player, plugin.mineRegistry().getMine(name), plugin);
                        })
        );
    }

    private void openMainGui(Player player, Mine mine, MinesExpanded plugin) {
        Gui mainMenu = Gui.gui()
                .title(MessageUtil.component("Editing: " + mine.getName()))
                .rows(5)
                .disableAllInteractions()
                .create();

        mainMenu.setItem(2, 3, ItemBuilder.from(Material.DIAMOND_PICKAXE)
                .name(MessageUtil.component("&6Reset Time"))
                .lore(
                        MessageUtil.component(""),
                        MessageUtil.component("&aCurrent: &7" + mine.getResetTime() + " seconds"),
                        MessageUtil.component(""),
                        MessageUtil.component("&7Set how often the mine will reset.")
                )
                .asGuiItem(event -> {
                    SignMenu.open(player, new String[]{String.valueOf(mine.getResetTime()), "^^^", "Time between each", "reset in seconds."}, lines -> {
                        try {
                            int time = Integer.parseInt(lines[0]);
                            mine.setResetTime(time);
                            saveMine(plugin, player, mine);
                        } catch (NumberFormatException e) {
                            player.sendMessage("Invalid number.");
                        }
                        Bukkit.getScheduler().runTask(plugin, () -> openMainGui(player, mine, plugin));
                    });
                }));

        mainMenu.setItem(2, 5, ItemBuilder.from(Material.DIAMOND)
                .name(MessageUtil.component("&6Reset Percentage"))
                .lore(
                        MessageUtil.component(""),
                        MessageUtil.component("&aCurrent: &7" + mine.getResetPercentage() + "%"),
                        MessageUtil.component(""),
                        MessageUtil.component("&7Set the percentage of the mine left before reset.")
                )
                .asGuiItem(event -> {
                    SignMenu.open(player, new String[]{String.valueOf(mine.getResetPercentage()), "^^^", "Percent left of", "mine until reset."}, lines -> {
                        try {
                            int percentage = Integer.parseInt(lines[0]);
                            mine.setResetPercentage(percentage);
                            saveMine(plugin, player, mine);
                        } catch (NumberFormatException e) {
                            player.sendMessage("Invalid number.");
                        }
                        Bukkit.getScheduler().runTask(plugin, () -> openMainGui(player, mine, plugin));
                    });
                }));

        mainMenu.setItem(2, 7, ItemBuilder.from(Material.ENDER_PEARL)
                .name(MessageUtil.component("&6Teleport Location"))
                .lore(
                        MessageUtil.component(""),
                        MessageUtil.component("&7Set the location to teleport to when the mine is reset.")
                )
                .asGuiItem(event -> {
                    mine.setTeleport(player.getLocation());
                    saveMine(plugin, player, mine);
                    openMainGui(player, mine, plugin);
                }));

        mainMenu.setItem(4, 3, ItemBuilder.from(Material.STONE)
                .name(MessageUtil.component("&6Blocks"))
                .lore(
                        MessageUtil.component(""),
                        MessageUtil.component("&7Add, remove, or edit blocks in the mine.")
                )
                .asGuiItem(event -> openBlocksGui(player, mine, plugin)));


        mainMenu.setItem(5, 1, ItemBuilder.from(Material.ARROW)
                .name(MessageUtil.component("&6Back"))
                .asGuiItem(event -> player.performCommand("mines")));

        mainMenu.open(player);
    }

    private void openBlocksGui(Player player, Mine mine, MinesExpanded plugin) {
        PaginatedGui blocksMenu = Gui.paginated()
                .title(MessageUtil.component("Editing Blocks: " + mine.getName()))
                .rows(5)
                .disableAllInteractions()
                .create();

        mine.getBlocks().forEach((material, percentage) -> blocksMenu.addItem(ItemBuilder.from(material)
                .name(MessageUtil.component("&6" + material.name() + " &7- " + percentage + "%"))
                .lore(
                        MessageUtil.component("&6Left-Click &7to edit percentage."),
                        MessageUtil.component("&6Right-Click &7to remove block.")
                )
                .asGuiItem(event -> {
                    if (event.isRightClick()) {
                        mine.getBlocks().remove(material);
                        saveMine(plugin, player, mine);
                        Bukkit.getScheduler().runTask(plugin, () -> openBlocksGui(player, mine, plugin));
                        return;
                    }
                    SignMenu.open(player, new String[]{String.valueOf(percentage), "^^^", "Percentage of", "block in mine."}, lines -> {
                        try {
                            float percent = Float.parseFloat(lines[0]);
                            mine.getBlocks().put(material, percent);
                            saveMine(plugin, player, mine);
                        } catch (NumberFormatException e) {
                            player.sendMessage("Invalid number.");
                        }
                        Bukkit.getScheduler().runTask(plugin, () -> openBlocksGui(player, mine, plugin));
                    });
                })));

        blocksMenu.setPlayerInventoryAction(event -> {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) return;

            Material material = item.getType();
            if (mine.hasBlock(material)) return;

            SignMenu.open(player, new String[]{"0", "^^^", "Percentage of", "block in mine."}, lines -> {
                try {
                    float percent = Float.parseFloat(lines[0]);
                    mine.getBlocks().put(material, percent);
                    saveMine(plugin, player, mine);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid number.");
                }
                Bukkit.getScheduler().runTask(plugin, () -> openBlocksGui(player, mine, plugin));
            });
        });

        blocksMenu.setItem(5, 1, ItemBuilder.from(Material.ARROW).name(MessageUtil.component("&6Back")).asGuiItem(event -> openMainGui(player, mine, plugin)));
        blocksMenu.setItem(5, 3, ItemBuilder.from(Material.PAPER).name(MessageUtil.component("&cPrevious")).asGuiItem(event -> blocksMenu.previous()));
        blocksMenu.setItem(5, 7, ItemBuilder.from(Material.PAPER).name(MessageUtil.component("&aNext")).asGuiItem(event -> blocksMenu.next()));

        blocksMenu.open(player);
    }

    private void saveMine(MinesExpanded plugin, Player player, Mine mine) {
        MineFile file = mine.getFile();
        file.save();

        MineServices services = plugin.mineServices();
        services.removeService(mine);
        services.addService(mine);

        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }
}
