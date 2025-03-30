package one.tranic.pfc.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.minecraftforge.fml.loading.FMLPaths;
import one.tranic.pfc.config.mods.CachedMain;
import one.tranic.pfc.config.mods.PlayData;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class Config {
    private static File configFile;
    private static YamlConfiguration configuration;

    private static CachedMain CACHED_MAIN;

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static synchronized void reload() {
        if (CACHED_MAIN != null && CACHED_MAIN.enabled() && CACHED_MAIN.cache() != null) {
            CACHED_MAIN.cache().invalidateAll();
            CACHED_MAIN = null;
        }
        configFile = getConfigDirectory().resolve("ProfileCached.yml").toFile();
        try {
            if (!configFile.exists()) {
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdir();
                }
                configFile.createNewFile();
            }
            configuration = YamlConfiguration.loadConfiguration(configFile);
            save();
            read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized void save() throws IOException {
        configuration.addDefault("enabled", true);
        configuration.addDefault("debug", false);
        configuration.addDefault("result-timeout", 1440);
        configuration.addDefault("verify-last-ip", false);

        configuration.options().copyDefaults(true);
        configuration.save(configFile);
    }

    private static synchronized void read() {
        var debug = configuration.getBoolean("debug");
        var enabled = configuration.getBoolean("enabled");
        var verifyLastIP = configuration.getBoolean("verify-last-ip");
        var resultTimeout = configuration.getInt("result-timeout");
        if (resultTimeout < 10) resultTimeout = 10;
        Cache<String, PlayData> cache;
        if (CACHED_MAIN != null) {
            if (CACHED_MAIN.enabled() != enabled || CACHED_MAIN.resultTimeout() != resultTimeout) {
                if (CACHED_MAIN.cache() != null)
                    CACHED_MAIN.cache().invalidateAll();
                cache = enabled ? createCache(resultTimeout) : null;
            } else {
                cache = CACHED_MAIN.cache();
            }
        } else {
            cache = enabled ? createCache(resultTimeout) : null;
        }
        CACHED_MAIN = new CachedMain(enabled, debug, verifyLastIP, resultTimeout, cache);
    }

    private static Cache<String, PlayData> createCache(int timeout) {
        return Caffeine.newBuilder()
                .expireAfterWrite(timeout, java.util.concurrent.TimeUnit.MINUTES)
                .build();
    }

    public static CachedMain getCachedMain() {
        return CACHED_MAIN;
    }
}
