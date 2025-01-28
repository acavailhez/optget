package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

public class DoubleCast extends AbstractNumberCast<Double> {

    @Override
    protected @NotNull Double valueFromNumber(Number number) {
        return number.doubleValue();
    }

    @Override
    protected @NotNull Double valueFromString(String string) {
        return Double.valueOf(string);
    }

    @Override
    public @NotNull Class<Double> getCastClass() {
        return Double.class;
    }
}
