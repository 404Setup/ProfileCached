package one.tranic.pfc.mixin;

import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import one.tranic.pfc.ProfileCached;
import one.tranic.pfc.config.Config;
import one.tranic.pfc.config.mods.PlayData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetAddress;

@Mixin(YggdrasilMinecraftSessionService.class)
public abstract class YggdrasilMinecraftSessionServiceMixin implements MinecraftSessionService {
    @Inject(method = "hasJoinedServer", at = @At("HEAD"), cancellable = true, remap = false)
    public void preHasJoinedServer(String profileName, String serverId, InetAddress address, CallbackInfoReturnable<ProfileResult> cir) throws AuthenticationUnavailableException {
        var config = Config.getCachedMain();
        if (config.enabled() && config.cache() != null) {
            var cached = config.cache().getIfPresent(profileName);
            if (cached != null) {
                if (config.verifyLastIP()
                        && address != null
                        && cached.lastLoginIP() != null
                        && !cached.lastLoginIP().equals(address.getHostAddress())
                ) {
                    if (config.debug())
                        ProfileCached.LOGGER.info("Hit Cache, but IPs do not match: username={}, serverId={}, address={}, cachedIP={}", profileName, serverId, address, cached.lastLoginIP());

                    // If the player's IP changes during the caching period, then invalidate the cache.
                    // Call return here to continue with the original validation logic.
                    config.cache().invalidate(profileName);
                    return;
                }
                if (config.debug())
                    ProfileCached.LOGGER.info("Hit Cache: username={}, serverId={}, address={}", profileName, serverId, address);
                cir.setReturnValue(cached.profile());
            }
        }
    }

    @Inject(method = "hasJoinedServer", at = @At("RETURN"), remap = false)
    public void postHasJoinedServer(String profileName, String serverId, InetAddress address, CallbackInfoReturnable<ProfileResult> cir) throws AuthenticationUnavailableException {
        ProfileResult result = cir.getReturnValue();
        var config = Config.getCachedMain();
        if (result != null && config.enabled() && config.cache() != null) {
            var playerAddress = address != null ? address.getHostAddress() : null;
            config.cache().put(profileName,
                    new PlayData(result,
                            config.verifyLastIP()
                                    ? playerAddress
                                    : null));
            if (config.debug())
                ProfileCached.LOGGER.info("Cache is saved: username={}, serverId={}, address={}", profileName, serverId, address);
        }
    }


}
