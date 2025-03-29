package one.tranic.pfc.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mojang.authlib.GameProfile;
import net.fabricmc.loader.api.FabricLoader;
import one.tranic.pfc.config.mods.CachedMain;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class Config {
    private static File configFile;
    private static YamlConfiguration configuration;

    private static CachedMain CACHED_MAIN;

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
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

        configuration.options().copyDefaults(true);
        configuration.save(configFile);
    }

    private static synchronized void read() {
        var debug = configuration.getBoolean("debug");
        var enabled = configuration.getBoolean("enabled");
        var resultTimeout = configuration.getInt("result-timeout");
        if (resultTimeout < 10) resultTimeout = 10;
        Cache<String, GameProfile> cache = enabled ? Caffeine.newBuilder()
                .expireAfterWrite(resultTimeout, java.util.concurrent.TimeUnit.MINUTES)
                .build() : null;
        CACHED_MAIN = new CachedMain(enabled, debug, resultTimeout, cache);
    }

    public static CachedMain getCachedMain() {
        return CACHED_MAIN;
    }
}
