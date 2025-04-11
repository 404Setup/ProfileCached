package one.tranic.pfc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import one.tranic.pfc.ProfileCached;
import one.tranic.pfc.config.Config;

@Mod.EventBusSubscriber(modid = ProfileCached.MODID)
@SuppressWarnings("unused")
public class ProfileCachedCommand {

    private static final Component DISABLED = Component.literal("ProfileCached is not enabled!");
    private static final Component RELOADED = Component.literal("ProfileCached has been reloaded!");

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var rootCommand = Commands.literal("pfc")
                .requires((source) -> source.hasPermission(3));

        rootCommand.then(Commands.literal("reload")
                .executes((ctx) -> reload(ctx.getSource()))
        );


        rootCommand.then(Commands.literal("cleanall")
                .executes(ProfileCachedCommand::clean)
        );

        rootCommand.then(Commands.literal("clean")
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(ctx.getSource().getServer().getPlayerNames(), builder))
                        .executes((ctx) -> cleanPlayer(ctx, StringArgumentType.getString(ctx, "player"))))
        );

        rootCommand.then(Commands.literal("size").executes(ProfileCachedCommand::size));

        dispatcher.register(rootCommand);
    }

    private static int reload(CommandSourceStack source) {
        Config.reload();

        source.sendSuccess(() -> RELOADED, true);
        return 1;
    }

    private static int clean(CommandContext<CommandSourceStack> ctx) {
        var source = ctx.getSource();
        var cache = Config.getCachedMain().cache();
        if (cache == null) {
            source.sendFailure(DISABLED);
            return 0;
        }
        cache.invalidateAll();
        source.sendSuccess(() -> Component.literal("All caches have been cleaned!"), true);
        return 1;
    }

    private static int cleanPlayer(CommandContext<CommandSourceStack> ctx, String player) {
        var source = ctx.getSource();
        if (Config.getCachedMain().cache() == null) {
            source.sendFailure(DISABLED);
            return 0;
        }
        var result = Config.getCachedMain().cache().getIfPresent(player);
        if (result == null) {
            source.sendFailure(Component.literal("Player " + player + " is not cached!"));
            return 0;
        }
        Config.getCachedMain().cache().invalidate(player);
        source.sendSuccess(() -> Component.literal("Cache for player " + player + " has been cleaned!"), true);
        return 1;
    }

    private static int size(CommandContext<CommandSourceStack> ctx) {
        var source = ctx.getSource();
        var cache = Config.getCachedMain().cache();
        if (cache == null) {
            source.sendFailure(DISABLED);
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Profile Cached Size: " + cache.estimatedSize()), true);
        return 1;
    }
}
