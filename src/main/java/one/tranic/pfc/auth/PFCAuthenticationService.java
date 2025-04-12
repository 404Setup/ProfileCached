package one.tranic.pfc.auth;

import com.mojang.authlib.Environment;
import com.mojang.authlib.EnvironmentParser;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import one.tranic.pfc.helpful.YggdrasilProxyHelpful;

import java.net.Proxy;

public class PFCAuthenticationService extends YggdrasilAuthenticationService {
    public PFCAuthenticationService(Proxy proxy) {
        super(YggdrasilProxyHelpful.determineProxy(proxy));
    }

    private static Environment determineEnvironment() {
        return EnvironmentParser
                .getEnvironmentFromProperties()
                .orElse(YggdrasilEnvironment.PROD.getEnvironment());
    }

    @Override
    public MinecraftSessionService createMinecraftSessionService() {
        return new PFCSessionService(this, determineEnvironment());
    }
}