package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractCast<T> {

    public @NotNull T cast(@NotNull final Object unknown) {
        return cast(unknown, CastMode.CLEAN);
    }

    public abstract @NotNull T cast(@NotNull final Object unknown, @NotNull final CastMode mode);

    public abstract @NotNull Class<T> getCastClass();
}
