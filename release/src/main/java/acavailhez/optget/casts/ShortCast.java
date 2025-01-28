package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

public class ShortCast extends AbstractNumberCast<Short> {

    @Override
    protected @NotNull Short valueFromNumber(Number number) {
        return number.shortValue();
    }

    @Override
    protected @NotNull Short valueFromString(String string) {
        return Short.valueOf(string);
    }

    @Override
    public @NotNull Class<Short> getCastClass() {
        return Short.class;
    }
}
