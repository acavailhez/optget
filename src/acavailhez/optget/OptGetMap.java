package acavailhez.optget;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Simple map wrapper exposing GetOpt function
public class OptGetMap implements Map<String, Object>, OptGet {

    private final Map<String, Object> map;

    public OptGetMap(Map<String, Object> map) {
        if (map != null) {
            this.map = map;
        } else {
            this.map = new HashMap<>();
        }
    }

    @Override
    public void onMissingKey(Object key, Class classToCast) {
        throw new IllegalArgumentException("Missing key:" + key + " of class:" + classToCast);
    }

    @Override
    public Object opt(String key) {
        return map.get(key);
    }

    // Implementation of Map

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }
}
