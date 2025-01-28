package acavailhez.optget.wraps;

import acavailhez.optget.OptGet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

// OptGet wrapper around a Map<?,?>
public class MapOptGet extends OptGet {

    private final Map<Object, Object> map;

    public MapOptGet(final Map<?, ?> map) {
        this.map = (Map<Object, Object>) map;
    }

    @Override
    protected @Nullable Object optToOverride(@NotNull Object key) {
        return map.get(key);
    }

    @Override
    public @Nullable Object put(Object key, Object value) {
        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Set<Object> keySet() {
        return (Set<Object>) map.keySet();
    }
}
