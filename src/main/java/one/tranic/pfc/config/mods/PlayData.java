package one.tranic.pfc.config.mods;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PlayData(@NotNull GameProfile profile, @Nullable String lastLoginIP) {
}
