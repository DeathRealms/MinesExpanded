package me.deathrealms.minesexpanded.util;

import me.deathrealms.minesexpanded.MinesExpanded;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {
    public static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-f])");
    private static final Pattern LEGACY_FORMATS_PATTERN = Pattern.compile("&[\\da-fk-or]|§[\\da-fk-or]");
    private static JSONObject LANG;

    public static void loadMessages(MinesExpanded plugin) {
        loadMessages(plugin, false);
    }

    public static void loadMessages(MinesExpanded plugin, boolean useResource) {
        if (!new File(plugin.getDataFolder(), "lang.json").exists()) {
            plugin.saveResource("lang.json", false);
        }

        try {
            File langFile = new File(plugin.getDataFolder(), "lang.json");
            InputStream langStream = useResource ? plugin.getResource("lang.json") : Files.newInputStream(langFile.toPath());
            Scanner scanner = new Scanner(langStream).useDelimiter("\\A");
            String jsonString = scanner.hasNext() ? scanner.next() : "{}";
            LANG = new JSONObject(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getMessage(Message key, Object... args) {
        String message = LANG.optString(key.getKey(), key.getKey());

        if (message.equals(key.getKey())) {
            loadMessages(MinesExpanded.instance(), true);
            message = LANG.optString(key.getKey(), key.getKey());
        }

        loadMessages(MinesExpanded.instance());
        return MessageFormat.format(message, args);
    }

    public static void message(CommandSender sender, String message) {
        MinesExpanded.instance().bukkitAudiences().sender(sender).sendMessage(component(getMessage(Message.PREFIX, message)));
    }

    public static void message(CommandSender sender, Message message, Object... args) {
        message(sender, message.getMessage(args));
    }

    public static Component component(String message) {
        return MiniMessage.miniMessage().deserialize(bungeeToMini(message));
    }

    public static String colorize(String message) {
        Component component = MiniMessage.miniMessage().deserialize(bungeeToMini(message));
        String legacyText = LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .build()
                .serialize(component);

        return translateHexCodes(legacyText);
    }

    private static String translateHexCodes(String textToTranslate) {

        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    private static String bungeeToMini(String message) {
        String parsed = message;
        Matcher matcher = LEGACY_FORMATS_PATTERN.matcher(message);

        while (matcher.find()) {
            final String before = parsed.substring(0, matcher.start());
            final String after = parsed.substring(matcher.end());

            final String group = matcher.group();
            final String code = group.replace("&", "").replace("§", "");
            final String color = ChatColor.getByChar(code.charAt(0)).name().toLowerCase();

            parsed = before + "<" + color + ">" + after;
            matcher = LEGACY_FORMATS_PATTERN.matcher(parsed);
        }

        return parsed;
    }
}
