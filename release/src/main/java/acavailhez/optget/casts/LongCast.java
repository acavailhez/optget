package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

public class LongCast extends AbstractNumberCast<Long> {

    @Override
    protected @NotNull Long valueFromNumber(Number number) {
        return number.longValue();
    }

    @Override
    protected @NotNull Long valueFromString(String string) {
        return Long.valueOf(string);
    }

    @Override
    public @NotNull Class<Long> getCastClass() {
        return Long.class;
    }
}
