package one.tranic.pfc.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import one.tranic.pfc.ProfileCached;
import one.tranic.pfc.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetAddress;

@Mixin(YggdrasilMinecraftSessionService.class)
public abstract class YggdrasilMinecraftSessionServiceMixin implements MinecraftSessionService {
    @Inject(method = "hasJoinedServer", at = @At("HEAD"), cancellable = true, remap = false)
    public void preHasJoinedServer(GameProfile user, String serverId, InetAddress address, CallbackInfoReturnable<GameProfile> cir) throws AuthenticationUnavailableException {
        if (Config.getCachedMain().enabled() && Config.getCachedMain().cache() != null) {
            var cached = Config.getCachedMain().cache().getIfPresent(user.getName());
            if (cached != null) {
                if (Config.getCachedMain().debug())
                    ProfileCached.LOGGER.info("Hit Cache: username={}, serverId={}, address={}", user.getName(), serverId, address);
                cir.setReturnValue(cached);
            }
        }
    }

    @Inject(method = "hasJoinedServer", at = @At("RETURN"), remap = false)
    public void postHasJoinedServer(GameProfile user, String serverId, InetAddress address, CallbackInfoReturnable<GameProfile> cir) throws AuthenticationUnavailableException {
        var result = cir.getReturnValue();
        if (result != null && Config.getCachedMain().enabled() && Config.getCachedMain().cache() != null) {
            Config.getCachedMain().cache().put(user.getName(), result);
            if (Config.getCachedMain().debug())
                ProfileCached.LOGGER.info("Cache is saved: username={}, serverId={}, address={}", user.getName(), serverId, address);
        }
    }


}
