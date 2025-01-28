package acavailhez.optget.casts;

import acavailhez.optget.OptGet;
import acavailhez.optget.wraps.MapOptGet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class OptGetCast extends AbstractCast<OptGet> {

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull OptGet cast(@NotNull Object unknown, @NotNull CastMode mode) {
        if (OptGet.class.isAssignableFrom(unknown.getClass())) {
            return (OptGet) unknown;
        }
        if (Map.class.isAssignableFrom(unknown.getClass())) {
            return new MapOptGet((Map) unknown);
        }
        throw new CastException(unknown, OptGet.class);
    }

    @Override
    public @NotNull Class<OptGet> getCastClass() {
        return OptGet.class;
    }
}
