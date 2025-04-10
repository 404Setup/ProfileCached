package one.tranic.pfc.auth;

import com.mojang.authlib.Environment;
import com.mojang.authlib.EnvironmentParser;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import one.tranic.pfc.ProfileCached;
import one.tranic.pfc.config.Config;
import one.tranic.pfc.config.mods.PlayData;
import one.tranic.pfc.helpful.YggdrasilProxyHelpful;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.Proxy;

public class PFCSessionService extends YggdrasilMinecraftSessionService {
    protected PFCSessionService(ServicesKeySet servicesKeySet, Proxy proxy, Environment env) {
        super(servicesKeySet, YggdrasilProxyHelpful.determineProxy(proxy), env);
    }

    public static MinecraftSessionService create(ServicesKeySet servicesKeySet, Proxy proxy) {
        return new PFCSessionService(servicesKeySet, proxy, determineEnvironment());
    }

    private static Environment determineEnvironment() {
        return EnvironmentParser
                .getEnvironmentFromProperties()
                .orElse(YggdrasilEnvironment.PROD.getEnvironment());
    }

    @Nullable
    public ProfileResult hasJoinedServer(final String profileName, final String serverId, @Nullable final InetAddress address) throws AuthenticationUnavailableException {
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
                    return postHasJoinedServer(super.hasJoinedServer(profileName, serverId, address), profileName, serverId, address);
                }
                if (config.debug())
                    ProfileCached.LOGGER.info("Hit Cache: username={}, serverId={}, address={}", profileName, serverId, address);
                return cached.profile();
            }
        }
        return postHasJoinedServer(super.hasJoinedServer(profileName, serverId, address), profileName, serverId, address);
    }

    public ProfileResult postHasJoinedServer(ProfileResult result, String profileName, String serverId, InetAddress address) throws AuthenticationUnavailableException {
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
        return result;
    }
}
