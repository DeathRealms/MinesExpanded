package me.deathrealms.minesexpanded;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.Region;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.deathrealms.minesexpanded.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MineService {
    private final Mine mine;
    private ScheduledTask task;

    public MineService(Mine mine) {
        this.mine = mine;
    }

    public void start() {
        if (task != null && !task.isCancelled()) return;

        int resetTime = mine.getResetTime();
        task = Bukkit.getAsyncScheduler().runAtFixedRate(MinesExpanded.instance(), (scheduledTask) -> resetMine(), resetTime, resetTime, TimeUnit.SECONDS);
    }

    public void stop() {
        if (task == null || task.isCancelled()) return;

        task.cancel();
        task = null;
    }

    public Mine getMine() {
        return this.mine;
    }

    public void resetMine() {
        Region region = mine.getRegion();
        if (region == null) return;

        Map<Material, Float> blocks = mine.getBlocks();
        if (blocks.isEmpty()) return;

        StringBuilder patternBuilder = new StringBuilder();

        for (Map.Entry<Material, Float> entry : blocks.entrySet()) {
            Material material = entry.getKey();
            float percentage = entry.getValue();
            patternBuilder.append(percentage).append("%").append(material.name()).append(",");
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(region.getWorld())) {
            ParserContext parserContext = new ParserContext();
            parserContext.setWorld(region.getWorld());
            parserContext.setActor(BukkitAdapter.adapt(Bukkit.getConsoleSender()));

            String input = patternBuilder.toString();
            input = input.substring(0, input.length() - 1);

            for (Entity entity : editSession.getEntities(region)) {
                Bukkit.getScheduler().runTask(MinesExpanded.instance(), () -> entity.setLocation(BukkitAdapter.adapt(mine.getTeleport())));
            }

            Pattern pattern = WorldEdit.getInstance().getPatternFactory().parseFromInput(input, parserContext);
            editSession.setBlocks(region, pattern);
        } catch (InputParseException e) {
            Logger.error("Failed to parse pattern: " + e.getMessage());
        } catch (MaxChangedBlocksException e) {
            Logger.error("Failed to reset mine: " + e.getMessage());
        }
    }
}
