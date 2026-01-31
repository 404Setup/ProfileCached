package one.tranic.pfc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import one.tranic.pfc.config.Config;

@SuppressWarnings("unused")
public class ProfileCachedCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var rootCommand = Commands.literal("pfc")
                .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN));

        rootCommand.then(Commands.literal("reload")
                .executes((ctx) -> reload(ctx.getSource()))
        );


        rootCommand.then(Commands.literal("cleanall")
                .executes(ProfileCachedCommand::clean)
        );


        rootCommand.then(Commands.literal("clean")
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (String playerName : ctx.getSource().getServer().getPlayerNames())
                                builder.suggest(playerName);
                            return builder.buildFuture();
                        }).executes((ctx) -> cleanPlayer(ctx, StringArgumentType.getString(ctx, "player"))))
        );
        rootCommand.then(Commands.literal("size").executes(ProfileCachedCommand::size));

        dispatcher.register(rootCommand);
    }

    private static int reload(CommandSourceStack source) {
        Config.reload();


        source.sendSuccess(() -> Component.literal("Profile Cached is reloaded!"), true);
        return 1;
    }

    private static int clean(CommandContext<CommandSourceStack> ctx) {
        var source = ctx.getSource();
        var cache = Config.getCachedMain().cache();
        if (cache == null) {
            source.sendFailure(Component.literal("Profile Cached is not enabled!"));
            return 0;
        }
        cache.invalidateAll();
        source.sendSuccess(() -> Component.literal("All caches have been cleaned!"), true);
        return 1;
    }

    private static int cleanPlayer(CommandContext<CommandSourceStack> ctx, String player) {
        var source = ctx.getSource();
        if (Config.getCachedMain().cache() == null) {
            source.sendFailure(Component.literal("Profile Cached is not enabled!"));
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
            source.sendFailure(Component.literal("Profile Cached is not enabled!"));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Profile Cached Size: " + cache.estimatedSize()), true);
        return 1;
    }
}
