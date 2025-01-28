package acavailhez.optget.casts;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListCast extends AbstractCast<List> {

    @Override
    public @NotNull List cast(@NotNull Object unknown, @NotNull CastMode mode) {
       throw new RuntimeException("");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull Class<List> getCastClass() {
        return List.class;
    }
}
