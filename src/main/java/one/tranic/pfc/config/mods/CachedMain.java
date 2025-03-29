package one.tranic.pfc.config.mods;

import com.github.benmanes.caffeine.cache.Cache;
import org.jetbrains.annotations.Nullable;

public record CachedMain(
        boolean enabled,
        boolean debug,
        boolean verifyLastIP,
        int resultTimeout,
        @Nullable Cache<String, PlayData> cache
) {
}