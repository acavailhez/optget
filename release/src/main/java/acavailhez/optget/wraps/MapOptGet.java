package acavailhez.optget.wraps;

import acavailhez.optget.OptGet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class MapOptGet extends OptGet {

    private final Map<?, ?> map;

    public MapOptGet(@NotNull final Map<?, ?> map) {
        this.map = map;
    }

    @Override
    protected @Nullable Object optToOverride(@NotNull Object key) {
        return map.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Set<Object> keySet() {
        return (Set<Object>) map.keySet();
    }
}
