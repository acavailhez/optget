package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

abstract class AbstractNumberCast<N extends Number> extends AbstractCast<N> {
    @Override
    @SuppressWarnings("unchecked")
    public @NotNull N cast(@NotNull final Object unknown, @NotNull final CastMode mode) {
        if (unknown.getClass().equals(this.getCastClass())) {
            return (N) unknown;
        }
        if (mode == CastMode.STRICT) {
            throw new CastException(unknown, this.getCastClass());
        }
        if (unknown instanceof Number) {
            return valueFromNumber((Number) unknown);
        }
        if (unknown instanceof String) {
            try {
                if (mode == CastMode.UNSAFE_BEST_EFFORT) {
                    String cleaned = ((String) unknown).replaceAll("[^0-9.-e]", "");
                    Number number = Double.parseDouble(cleaned);
                    return valueFromNumber(number);
                }
                if (mode == CastMode.CLEAN) {
                    String cleaned = ((String) unknown).replaceAll("[^0-9.-e]", "");
                    return valueFromString(cleaned);
                }
                return valueFromString((String) unknown);
            } catch (final NumberFormatException e) {
                throw new CastException(unknown, Integer.class, e);
            }
        }
        throw new CastException(unknown, Integer.class);
    }

    protected abstract @NotNull N valueFromNumber(Number number);

    protected abstract @NotNull N valueFromString(String string);

    protected abstract @NotNull Class<N> getCastClass();
}
