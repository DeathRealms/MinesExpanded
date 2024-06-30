package me.deathrealms.minesexpanded;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class Mine {
    private final String name;
    private int resetTime = 600;
    private int resetPercentage = 35;
    private Location teleport;
    private Location min;
    private Location max;
    private final Map<Material, Float> blocks = new HashMap<>();

    public Mine(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getResetTime() {
        return this.resetTime;
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    public int getResetPercentage() {
        return this.resetPercentage;
    }

    public void setResetPercentage(int resetPercentage) {
        this.resetPercentage = resetPercentage;
    }

    public Location getTeleport() {
        return this.teleport;
    }

    public void setTeleport(Location teleport) {
        this.teleport = teleport;
    }

    public Region getRegion() {
        return new CuboidRegion(BukkitAdapter.adapt(this.min.getWorld()), BukkitAdapter.asBlockVector(this.min), BukkitAdapter.asBlockVector(this.max));
    }

    public Location getMin() {
        return this.min;
    }

    public void setMin(Location min) {
        this.min = min;
    }

    public Location getMax() {
        return this.max;
    }

    public void setMax(Location max) {
        this.max = max;
    }

    public Map<Material, Float> getBlocks() {
        return this.blocks;
    }

    public void setBlocks(Map<Material, Float> blocks) {
        this.blocks.clear();
        this.blocks.putAll(blocks);
    }

    public void addBlock(Material material, float percentage) {
        this.blocks.put(material, percentage);
    }

    public void removeBlock(Material material) {
        this.blocks.remove(material);
    }

    public void clearBlocks() {
        this.blocks.clear();
    }

    public boolean hasBlock(Material material) {
        return this.blocks.containsKey(material);
    }

    public float getBlockPercentage(Material material) {
        return this.blocks.get(material);
    }

    public void setBlockPercentage(Material material, float percentage) {
        this.blocks.put(material, percentage);
    }
}
