package one.tranic.pfc.auth;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import one.tranic.pfc.helpful.YggdrasilProxyHelpful;

import java.lang.reflect.Field;
import java.net.Proxy;

public class PFCAuthenticationService extends YggdrasilAuthenticationService {
    public PFCAuthenticationService(Proxy proxy) {
        super(YggdrasilProxyHelpful.determineProxy(proxy));
    }

    private ServicesKeySet determineServicesKeySet() {
        try {
            Field field = YggdrasilAuthenticationService.class.getDeclaredField("servicesKeySet");
            field.setAccessible(true);
            return (ServicesKeySet) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MinecraftSessionService createMinecraftSessionService() {
        return PFCSessionService.create(determineServicesKeySet(), getProxy());
    }
}