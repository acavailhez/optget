package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

public class BooleanCast extends AbstractCast<Boolean> {

    @Override
    public @NotNull Boolean cast(@NotNull Object unknown, @NotNull CastMode mode) {
        if (Boolean.class.isAssignableFrom(unknown.getClass())) {
            return (Boolean) unknown;
        }
        String string = unknown.toString().toLowerCase();
        if (mode == CastMode.PARSE || mode == CastMode.UNSAFE_BEST_EFFORT) {
            if ("true".equals(string)) {
                return true;
            }
            if ("false".equals(string)) {
                return false;
            }
            if ("0".equals(string)) {
                return false;
            }
            if ("1".equals(string)) {
                return true;
            }
        }
        throw new CastException(unknown, Boolean.class);
    }

    @Override
    public @NotNull Class<Boolean> getCastClass() {
        return Boolean.class;
    }
}
