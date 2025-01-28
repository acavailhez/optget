package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

public class FloatCast extends AbstractNumberCast<Float> {

    @Override
    protected @NotNull Float valueFromNumber(Number number) {
        return number.floatValue();
    }

    @Override
    protected @NotNull Float valueFromString(String string) {
        return Float.valueOf(string);
    }

    @Override
    public @NotNull Class<Float> getCastClass() {
        return Float.class;
    }
}
