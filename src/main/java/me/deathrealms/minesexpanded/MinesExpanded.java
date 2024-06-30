package me.deathrealms.minesexpanded;

import me.deathrealms.minesexpanded.command.MinesCommand;
import me.deathrealms.minesexpanded.util.Message;
import me.deathrealms.minesexpanded.util.MessageUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.minecraft.extras.caption.ComponentCaptionFormatter;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.text.MessageFormat;

public final class MinesExpanded extends JavaPlugin {
    private BukkitAudiences bukkitAudiences;
    private MinecraftHelp<CommandSender> minecraftHelp;
    private MineRegistry mineRegistry;
    private static MinesExpanded instance;

    @Override
    public void onEnable() {
        instance = this;

        MessageUtil.loadMessages(this);
        this.mineRegistry = new MineRegistry();
        this.mineRegistry.registerMines();

        LegacyPaperCommandManager<CommandSender> commandManager = new LegacyPaperCommandManager<>(this, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity());

        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier();
        } else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }

        this.bukkitAudiences = BukkitAudiences.create(this);

        MinecraftExceptionHandler.create(this.bukkitAudiences::sender)
                .defaultInvalidSyntaxHandler()
                .defaultInvalidSenderHandler()
                .defaultNoPermissionHandler()
                .defaultArgumentParsingHandler()
                .defaultCommandExecutionHandler()
                .decorator(component -> Component.text().append(MessageUtil.component(MessageFormat.format(Message.PREFIX.getMessage(), ""))).append(component).build())
                .registerTo(commandManager);

        this.minecraftHelp = MinecraftHelp.<CommandSender>builder()
                .commandManager(commandManager)
                .audienceProvider(this.bukkitAudiences::sender)
                .commandPrefix("/mines help")
                .messageProvider(MinecraftHelp.captionMessageProvider(
                        commandManager.captionRegistry(),
                        ComponentCaptionFormatter.miniMessage()
                ))
                .build();
        commandManager.captionRegistry().registerProvider(MinecraftHelp.defaultCaptionsProvider());

        new MinesCommand(this, commandManager);
    }

    @Override
    public void onDisable() {
    }

    public BukkitAudiences bukkitAudiences() {
        return this.bukkitAudiences;
    }

    public MinecraftHelp<CommandSender> minecraftHelp() {
        return this.minecraftHelp;
    }

    public MineRegistry mineRegistry() {
        return this.mineRegistry;
    }

    public static MinesExpanded instance() {
        return instance;
    }
}
