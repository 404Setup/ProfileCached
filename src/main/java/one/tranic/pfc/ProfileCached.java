package one.tranic.pfc;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import one.tranic.pfc.config.Config;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ProfileCached.MODID)
public class ProfileCached {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "profile_cached";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger("Profile Cached");

    public ProfileCached(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.warn("ProfileCached is not compatible with non-OnlineMode accounts!");
        LOGGER.warn("(i.e. accounts that do not perform account verification, or accounts that use the third-party Yggdrasil verification server)");
        Config.reload();
    }
}
