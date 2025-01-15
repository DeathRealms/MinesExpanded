package me.deathrealms.minesexpanded;

import me.deathrealms.minesexpanded.util.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MineFile {
    private final Mine mine;
    private final File file;
    private FileConfiguration config;

    public MineFile(Mine mine) {
        this.mine = mine;
        this.file = new File(MinesExpanded.instance().getDataFolder() + "/mines", mine.getName() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public void create() {
        if (this.file.exists()) return;

        try {
            this.file.getParentFile().mkdirs();
            this.config.save(this.file);
            reload();
        } catch (IOException e) {
            Logger.error("Failed to create file: " + this.file);
            e.printStackTrace();
        }
    }

    public void delete() {
        if (!this.file.exists()) return;
        this.file.delete();
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() {
        try {
            this.config.set("name", this.mine.getName());
            this.config.set("resetTime", this.mine.getResetTime());
            this.config.set("resetPercentage", this.mine.getResetPercentage());
            this.config.set("teleport", this.mine.getTeleport());
            this.config.set("min", this.mine.getMin());
            this.config.set("max", this.mine.getMax());
            this.config.set("blocks", null);
            this.mine.getBlocks().forEach((material, percentage) -> this.config.set("blocks." + material.name(), percentage));

            this.config.save(this.file);
        } catch (IOException e) {
            Logger.error("Failed to save file: " + this.file);
            e.printStackTrace();
        }
    }

    public FileConfiguration config() {
        return this.config;
    }
}
