package me.deathrealms.minesexpanded;

import java.util.HashMap;
import java.util.Map;

public class MineServices {
    private final MineRegistry mineRegistry;
    private final Map<Mine, MineService> SERVICES = new HashMap<>();

    public MineServices(MineRegistry mineRegistry) {
        this.mineRegistry = mineRegistry;
    }

    public MineService getService(Mine mine) {
        return this.SERVICES.get(mine);
    }

    public void addService(Mine mine) {
        MineService service = new MineService(mine);
        service.start();
        this.SERVICES.put(mine, service);
    }

    public void removeService(Mine mine) {
        this.SERVICES.remove(mine);
    }

    public boolean hasService(Mine mine) {
        return this.SERVICES.containsKey(mine);
    }

    public Map<Mine, MineService> getServices() {
        return this.SERVICES;
    }

    public void registerServices() {
        for (Mine mine : mineRegistry.getMines().values()) {
            addService(mine);
        }
    }
}
