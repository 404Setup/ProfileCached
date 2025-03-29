package one.tranic.pfc.config.mods;

import com.github.benmanes.caffeine.cache.Cache;
import com.mojang.authlib.GameProfile;

public record CachedMain(boolean enabled, boolean debug, int resultTimeout, Cache<String, GameProfile> cache) {
}
