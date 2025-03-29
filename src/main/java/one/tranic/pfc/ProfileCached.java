package one.tranic.pfc;

import net.fabricmc.api.ModInitializer;
import one.tranic.pfc.command.ProfileCachedCommand;
import one.tranic.pfc.config.Config;
import org.slf4j.Logger;

public class ProfileCached implements ModInitializer {
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger("Profile Cached");

    @Override
    public void onInitialize() {
        LOGGER.warn("ProfileCached is not compatible with non-OnlineMode accounts!");
        LOGGER.warn("(i.e. accounts that do not perform account verification, or accounts that use the third-party Yggdrasil verification server)");
        Config.reload();
        ProfileCachedCommand.register();
    }
}
