package acavailhez.optget;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Simple map wrapper exposing GetOpt function
public class OptGetMap{

//    private final Map<String, Object> map;
//
//    public OptGetMap(Map map) {
//        if (map != null) {
//            this.map = new HashMap<>();
//            for (Object o : map.entrySet()) {
//                Entry entry = (Entry) o;
//                this.map.put(entry.getKey().toString(), entry.getValue());
//            }
//        } else {
//            this.map = new HashMap<>();
//        }
//    }
//
//    @Override
//    public Object optToOverride(String key) {
//        return map.get(key);
//    }
//
//    // Implementation of Map
//
//    @Override
//    public int size() {
//        return map.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return map.isEmpty();
//    }
//
//    @Override
//    public boolean containsKey(@NotNull Object key) {
//        return map.containsKey(key);
//    }
//
//    @Override
//    public boolean containsValue(@NotNull Object value) {
//        return map.containsValue(value);
//    }
//
//    @Override
//    // Here we do not proxy to map, we use the OptGet instead
//    public Object get(@NotNull Object key) {
//        return super.get(key);
//    }
//
//    @Override
//    public Object put(String key, Object value) {
//        return map.put(key, value);
//    }
//
//    @Override
//    public Object remove(@NotNull Object key) {
//        return map.remove(key);
//    }
//
//    @Override
//    public void putAll(Map<? extends String, ?> m) {
//        map.putAll(m);
//    }
//
//    @Override
//    public void clear() {
//        map.clear();
//    }
//
//    @Override
//    public Set<String> keySet() {
//        return map.keySet();
//    }
//
//    @Override
//    public @NotNull Collection<Object> values() {
//        return map.values();
//    }
//
//    @Override
//    public @NotNull Set<Entry<String, Object>> entrySet() {
//        return map.entrySet();
//    }
}
