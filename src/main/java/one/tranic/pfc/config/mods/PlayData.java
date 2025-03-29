package one.tranic.pfc.config.mods;

import com.mojang.authlib.yggdrasil.ProfileResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PlayData(@NotNull ProfileResult profile, @Nullable String lastLoginIP) {
}