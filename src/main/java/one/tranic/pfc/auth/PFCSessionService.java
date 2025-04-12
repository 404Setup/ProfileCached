package one.tranic.pfc.auth;

import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import one.tranic.pfc.ProfileCached;
import one.tranic.pfc.config.Config;
import one.tranic.pfc.config.mods.PlayData;

import javax.annotation.Nullable;
import java.net.InetAddress;

public class PFCSessionService extends YggdrasilMinecraftSessionService {
    protected PFCSessionService(YggdrasilAuthenticationService service, Environment env) {
        super(service, env);
    }

    @Nullable
    public GameProfile hasJoinedServer(final GameProfile user, final String serverId, final InetAddress address) throws AuthenticationUnavailableException {
        var config = Config.getCachedMain();
        var profileName = user.getName();
        if (config.enabled() && config.cache() != null) {
            var cached = config.cache().getIfPresent(profileName);
            if (cached != null) {
                if (config.verifyLastIP()
                        && address != null
                        && cached.lastLoginIP() != null
                        && !cached.lastLoginIP().equals(address.getHostAddress())
                ) {
                    if (config.debug())
                        ProfileCached.LOGGER.info(
                                "Hit Cache, but IPs do not match: username={}, serverId={}, address={}, cachedIP={}",
                                profileName, serverId, address.getHostAddress(), cached.lastLoginIP());

                    // If the player's IP changes during the caching period, then invalidate the cache.
                    // Call return here to continue with the original validation logic.
                    config.cache().invalidate(profileName);
                    return postHasJoinedServer(super.hasJoinedServer(user, serverId, address),
                            profileName, serverId, address);
                }
                if (config.debug())
                    ProfileCached.LOGGER.info("Hit Cache: username={}, serverId={}, address={}",
                            user.getName(), serverId, cached.lastLoginIP());
                return cached.profile();
            }
        }
        return postHasJoinedServer(super.hasJoinedServer(user, serverId, address), profileName, serverId, address);
    }

    public GameProfile postHasJoinedServer(GameProfile profile, String profileName, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        var config = Config.getCachedMain();
        if (profile != null && config.enabled() && config.cache() != null) {
            var playerAddress = address != null ? address.getHostAddress() : null;
            config.cache().put(profile.getName(),
                    new PlayData(profile,
                            config.verifyLastIP()
                                    ? playerAddress
                                    : null));
            if (config.debug())
                ProfileCached.LOGGER.info("Cache is saved: username={}, serverId={}, address={}", profileName,
                        serverId, playerAddress
                );
        }
        return profile;
    }
}