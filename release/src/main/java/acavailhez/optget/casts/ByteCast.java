package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

public class ByteCast extends AbstractNumberCast<Byte> {

    @Override
    protected @NotNull Byte valueFromNumber(Number number) {
        return number.byteValue();
    }

    @Override
    protected @NotNull Byte valueFromString(String string) {
        return Byte.valueOf(string);
    }

    @Override
    public @NotNull Class<Byte> getCastClass() {
        return Byte.class;
    }
}
