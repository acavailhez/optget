package acavailhez.optget.wraps;

import acavailhez.optget.OptGet;
import acavailhez.optget.casts.CastMode;
import acavailhez.optget.casts.IntegerCast;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// OptGet wrapper around a List<?>
public class ListOptGet extends OptGet {

    private final List<?> list;
    private final static IntegerCast castor = new IntegerCast();

    public ListOptGet(@NotNull final List<?> list) {
        this.list = list;
    }

    @Override
    protected @Nullable Object optToOverride(@NotNull Object key) {
        Integer index = castor.cast(key, CastMode.CLEAN);
        return list.get(index);
    }

    @Override
    public @NotNull Set<Object> keySet() {
        Set<Object> keys = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            keys.add(i);
        }
        return keys;
    }
}
