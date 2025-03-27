package one.tranic.pfc.config.mods;

import com.github.benmanes.caffeine.cache.Cache;
import com.mojang.authlib.yggdrasil.ProfileResult;

public record CachedMain(boolean enabled, boolean debug, int resultTimeout, Cache<String, ProfileResult> cache) {
}
