package me.deathrealms.minesexpanded;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MineRegistry {
    private final Map<String, Mine> MINES = new HashMap<>();

    public Mine getMine(String name) {
        return this.MINES.get(name);
    }

    public void addMine(Mine mine) {
        this.MINES.put(mine.getName(), mine);
    }

    public void removeMine(String name) {
        this.MINES.remove(name);
    }

    public boolean hasMine(String name) {
        return this.MINES.containsKey(name);
    }

    public void registerMines() {
        File minesFolder = new File(MinesExpanded.instance().getDataFolder() + "/mines");
        if (!minesFolder.exists()) return;

        for (File file : minesFolder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                Mine mine = new Mine(file.getName().replace(".yml", ""));
                MineFile mineFile = new MineFile(mine);

                FileConfiguration config = mineFile.config();
                mine.setResetTime(config.getInt("resetTime"));
                mine.setResetPercentage(config.getInt("resetPercentage"));
                mine.setTeleport(config.getLocation("teleport", config.getLocation("max")));
                mine.setMin(config.getLocation("min"));
                mine.setMax(config.getLocation("max"));

                Map<Material, Float> blocks = new HashMap<>();
                ConfigurationSection blocksSection = config.getConfigurationSection("blocks");

                if (blocksSection != null) {
                    for (String key : blocksSection.getKeys(false)) {
                        blocks.put(Material.getMaterial(key), (float) blocksSection.getDouble(key));
                    }
                    mine.setBlocks(blocks);
                }

                addMine(mine);
            }
        }
    }
}
