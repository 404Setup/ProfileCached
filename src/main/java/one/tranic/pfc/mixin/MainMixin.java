package one.tranic.pfc.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.server.Main;
import net.minecraft.server.Services;
import one.tranic.pfc.auth.PFCAuthenticationService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.Proxy;

@Mixin(Main.class)
public class MainMixin {
    @Redirect(
            method = "main",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/Services;create(Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;Ljava/io/File;)Lnet/minecraft/server/Services;"
            )
    )
    private static Services createServices(YggdrasilAuthenticationService authenticationService, java.io.File file) {
        return Services.create(new PFCAuthenticationService(Proxy.NO_PROXY), file);
    }
}