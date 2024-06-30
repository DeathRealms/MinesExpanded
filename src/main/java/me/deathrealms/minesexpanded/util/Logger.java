package me.deathrealms.minesexpanded.util;

import me.deathrealms.minesexpanded.MinesExpanded;

public class Logger {
    private static final java.util.logging.Logger LOGGER = MinesExpanded.instance().getLogger();

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warning(String message) {
        LOGGER.warning(message);
    }

    public static void error(String message) {
        LOGGER.severe(message);
    }
}
