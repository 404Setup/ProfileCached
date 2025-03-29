package one.tranic.pfc;

import net.fabricmc.api.ModInitializer;
import one.tranic.pfc.command.ProfileCachedCommand;
import one.tranic.pfc.config.Config;
import org.slf4j.Logger;

public class ProfileCached implements ModInitializer {
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger("Profile Cached");

    @Override
    public void onInitialize() {
        /*if (!checkAuthenticationEnabled())
            throw new UnsupportedAccountException();*/

        LOGGER.warn("ProfileCached is not compatible with non-OnlineMode accounts!");
        LOGGER.warn("(i.e. accounts that do not perform account verification, or accounts that use the third-party Yggdrasil verification server)");
        Config.reload();
        ProfileCachedCommand.register();
    }

    /*private boolean checkAuthenticationEnabled() {
        // Read the online-mode value from server.properties
        try {
            Path serverPropertiesPath = FabricLoader.getInstance().getGameDir().resolve("server.properties");
            Properties properties = new Properties();

            if (serverPropertiesPath.toFile().exists()) {
                try (InputStream input = Files.newInputStream(serverPropertiesPath)) {
                    properties.load(input);
                }
                return Boolean.parseBoolean(properties.getProperty("online-mode", "true"));
            } else {
                LOGGER.warn("server.properties file not found.");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read server.properties file.", e);
        }

        return false;
    }*/
}
