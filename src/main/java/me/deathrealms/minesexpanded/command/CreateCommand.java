package me.deathrealms.minesexpanded.command;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import me.deathrealms.minesexpanded.Mine;
import me.deathrealms.minesexpanded.MineFile;
import me.deathrealms.minesexpanded.MinesExpanded;
import me.deathrealms.minesexpanded.util.Message;
import me.deathrealms.minesexpanded.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;

public class CreateCommand implements MECommand {

    @Override
    public void registerCommand(MinesExpanded plugin, BukkitCommandManager<CommandSender> manager) {
        manager.command(
                manager.commandBuilder("mines")
                        .literal("create")
                        .commandDescription(Description.of("Create a new mine."))
                        .senderType(Player.class)
                        .required("name", StringParser.stringParser())
                        .handler(context -> {
                            Player player = context.sender();
                            String name = context.get("name");

                            World world = BukkitAdapter.adapt(player.getWorld());

                            SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
                            LocalSession session = sessionManager.get(BukkitAdapter.adapt(player));

                            if (!session.isSelectionDefined(world)) {
                                MessageUtil.message(player, Message.CREATE_NO_SELECTION);
                                return;
                            }

                            try {
                                Region region = session.getSelection(world);

                                if (plugin.mineRegistry().hasMine(name)) {
                                    MessageUtil.message(player, Message.CREATE_EXISTS, name);
                                    return;
                                }

                                Mine mine = new Mine(name);
                                MineFile file = new MineFile(mine);

                                mine.setMin(BukkitAdapter.adapt(player.getWorld(), region.getMinimumPoint()));
                                mine.setMax(BukkitAdapter.adapt(player.getWorld(), region.getMaximumPoint()));
                                mine.setTeleport(mine.getMax().clone().add(0, 1, 0));

                                file.create();
                                file.save();

                                plugin.mineRegistry().addMine(mine);
                                plugin.mineServices().addService(mine);
                                MessageUtil.message(player, Message.CREATE_SUCCESS, name);
                            } catch (Exception e) {
                                e.printStackTrace();
                                MessageUtil.message(player, "&cAn error occurred while creating the mine. Check the console for more information.");
                            }
                        })
        );
    }
}
