package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

public class IntegerCast extends AbstractNumberCast<Integer> {

    @Override
    protected @NotNull Integer valueFromNumber(Number number) {
        return number.intValue();
    }

    @Override
    protected @NotNull Integer valueFromString(String string) {
        return Integer.valueOf(string);
    }

    @Override
    public @NotNull Class<Integer> getCastClass() {
        return Integer.class;
    }
}
