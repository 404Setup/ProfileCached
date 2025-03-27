package one.tranic.pfc.mixin;

import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
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
    public void preHasJoinedServer(String profileName, String serverId, InetAddress address, CallbackInfoReturnable<ProfileResult> cir) throws AuthenticationUnavailableException {
        if (Config.getCachedMain().enabled() && Config.getCachedMain().cache() != null) {
            ProfileResult cached = Config.getCachedMain().cache().getIfPresent(profileName);
            if (cached != null) {
                if (Config.getCachedMain().debug())
                    ProfileCached.LOGGER.info("Hit Cache: username={}, serverId={}, address={}", profileName, serverId, address);
                cir.setReturnValue(cached);
            }
        }
    }

    @Inject(method = "hasJoinedServer", at = @At("RETURN"), remap = false)
    public void postHasJoinedServer(String profileName, String serverId, InetAddress address, CallbackInfoReturnable<ProfileResult> cir) throws AuthenticationUnavailableException {
        ProfileResult result = cir.getReturnValue();
        if (result != null && Config.getCachedMain().enabled() && Config.getCachedMain().cache() != null) {
            Config.getCachedMain().cache().put(profileName, result);
            if (Config.getCachedMain().debug())
                ProfileCached.LOGGER.info("Cache is saved: username={}, serverId={}, address={}", profileName, serverId, address);
        }
    }


}
