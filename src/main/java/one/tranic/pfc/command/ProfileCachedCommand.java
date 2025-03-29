package one.tranic.pfc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import one.tranic.pfc.config.Config;

@SuppressWarnings("unused")
public class ProfileCachedCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = CommandManager.literal("pfc")
                .requires((source) -> source.hasPermissionLevel(3));

        rootCommand.then(CommandManager.literal("reload")
                .executes((ctx) -> reload(ctx.getSource()))
        );


        rootCommand.then(CommandManager.literal("cleanall")
                .executes(ProfileCachedCommand::clean)
        );

        rootCommand.then(CommandManager.literal("clean")
                .then(CommandManager.argument("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> CommandSource.suggestMatching(ctx.getSource().getServer().getPlayerNames(), builder))
                        .executes((ctx) -> cleanPlayer(ctx, StringArgumentType.getString(ctx, "player"))))
        );

        dispatcher.register(rootCommand);
    }

    private static int reload(ServerCommandSource source) {
        Config.reload();

        source.sendFeedback(Text.of("Profile Cached Profile is reloaded!"), true);
        return 1;
    }

    private static int clean(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        var cache = Config.getCachedMain().cache();
        if (cache == null) {
            source.sendFeedback(Text.of("Profile Cached Profile is not enabled!"), true);
            return 0;
        }
        cache.invalidateAll();
        source.sendFeedback(Text.of("All caches have been cleaned!"), true);
        return 1;
    }

    private static int cleanPlayer(CommandContext<ServerCommandSource> ctx, String player) {
        var source = ctx.getSource();
        if (Config.getCachedMain().cache() == null) {
            source.sendFeedback(Text.of("Profile Cached Profile is not enabled!"), true);
            return 0;
        }
        var result = Config.getCachedMain().cache().getIfPresent(player);
        if (result == null) {
            source.sendFeedback(Text.of("Player " + player + " is not cached!"), true);
            return 0;
        }
        Config.getCachedMain().cache().invalidate(player);
        source.sendFeedback(Text.of("Cache for player " + player + " has been cleaned!"), true);
        return 1;
    }
}
