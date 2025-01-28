package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

public class StringCast extends AbstractCast<String> {

    @Override
    public @NotNull String cast(@NotNull Object unknown, @NotNull CastMode mode) {
        if (String.class.isAssignableFrom(unknown.getClass())) {
            return (String) unknown;
        }
        return unknown.toString();
    }

    @Override
    public @NotNull Class<String> getCastClass() {
        return String.class;
    }
}
