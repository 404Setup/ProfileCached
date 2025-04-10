package one.tranic.pfc.mixin.client;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import one.tranic.pfc.auth.PFCAuthenticationService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.Proxy;

@Mixin(net.minecraft.client.Minecraft.class)
public abstract class ClientMinecraftMixin {
    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "com/mojang/authlib/yggdrasil/YggdrasilAuthenticationService"
            )
    )
    private YggdrasilAuthenticationService redirectAuthenticationService(Proxy proxy) {
        return new PFCAuthenticationService(proxy);
    }

}
