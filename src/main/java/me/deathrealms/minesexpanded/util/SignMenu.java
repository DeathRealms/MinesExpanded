package me.deathrealms.minesexpanded.util;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.exception.SignGUIVersionException;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public class SignMenu {

    public static void open(Player player, String[] lines, Consumer<String[]> callback) {
        try {
            SignGUI.builder()
                    .setLines(lines)
                    .setHandler((p, result) -> List.of(SignGUIAction.run(() -> callback.accept(result.getLines()))))
                    .build()
                    .open(player);
        } catch (SignGUIVersionException e) {
            Logger.error("Unable to open sign menu due to incompatible server version.");
        }
    }
}
