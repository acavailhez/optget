package acavailhez.optget;

// GENERATE:IMPORTS

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// OptGet wraps an object that only answers to a `public Object opt(Object key) throws Exception` method
// and exposes many shortcut functions to cast objects in desirable formats
public interface OptGet {

    // The main function to override
    // Can return null
    Object opt(String key);

    // What to do when a key is missing
    // Typically, throw an IllegalArgumentException
    // but your application might need something else
    void onMissingKey(Object key, Class classToCast);


    // #####################
    //  OptGet Tooling
    // #####################

    default <T> T opt(String key, Class<T> classToCast) {
        return opt(key, classToCast, null);
    }

    @SuppressWarnings("unchecked")
    default <T> T opt(String key, Class<T> classToCast, T defaultValue) {
        Object nonCast = recursiveOpt(key);
        if (nonCast == null) {
            return defaultValue;
        }
        return CastUtils.cast(nonCast, classToCast);
    }

    // Will transform getString("key.sub") to getGetOpt("key").getString("sub")
    // when used in groovy, map.key.sub will then work
    default Object recursiveOpt(Object key) {
        Object value = opt(key.toString());
        if (value != null) {
            return value;
        }
        // Search recursively in the underlying opt object
        String stringKey = key.toString();
        String[] subkeys = stringKey.split("\\.");
        OptGet optGet = this;
        for (int i = 0; i < subkeys.length - 1; i++) {
            optGet = optGet.opt(subkeys[i], OptGet.class);
            if (optGet == null) {
                return null;
            }
        }
        return optGet.opt(subkeys[subkeys.length - 1]);
    }

    default Object get(String key) {
        return get(key, Object.class);
    }

    default <T> T get(String key, Class<T> classToCast) {
        T value = opt(key, classToCast);
        if (value == null) {
            onMissingKey(key, classToCast);
        }
        return value;
    }


    // #####################
    //  Shortcuts
    // #####################

    // Simple shortcuts

    // GENERATE:SIMPLE-SHORTCUTS

    default <ENUM extends Enum> ENUM optEnum(String key, Class<ENUM> enumClass) {
        return opt(key, enumClass);
    }

    default <ENUM extends Enum> ENUM getEnum(String key, Class<ENUM> enumClass) {
        return get(key, enumClass);
    }

    // List shortcuts

    default List optList(String key) {
        return opt(key, List.class);
    }

    default List getList(String key) {
        return get(key, List.class);
    }

    default <T> List<T> optList(String key, Class<T> classToCast) {
        List list = opt(key, List.class);
        List<T> listCasted = new LinkedList<T>();
        for (Object o : list) {
            listCasted.add(CastUtils.cast(o, classToCast));
        }
        return listCasted;
    }

    default <T> List<T> getList(String key, Class<T> classToCast) {
        List<T> value = optList(key, classToCast);
        if (value == null) {
            onMissingKey(key, classToCast);
        }
        return value;
    }

    // GENERATE:LIST-SHORTCUTS

    default <ENUM extends Enum> List<ENUM> optListEnum(String key, Class<ENUM> enumClass) {
        return optList(key, enumClass);
    }

    default <ENUM extends Enum> List<ENUM> getListEnum(String key, Class<ENUM> enumClass) {
        return getList(key, enumClass);
    }

    // Map shortcuts

    default Map optMap(String key) {
        return opt(key, Map.class);
    }

    default Map getMap(String key) {
        return get(key, Map.class);
    }

    default <KEY, VALUE> Map<KEY, VALUE> optMap(String key, Class<KEY> keyToCast, Class<VALUE> valueToCast) {
        Map map = opt(key, Map.class);
        Map<KEY, VALUE> mapCasted = new HashMap<>();
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            mapCasted.put(CastUtils.cast(entry.getKey(), keyToCast), CastUtils.cast(entry.getValue(), valueToCast));
        }
        return mapCasted;
    }

    default <KEY, VALUE> Map<KEY, VALUE> getMap(String key, Class<KEY> keyToCast, Class<VALUE> valueToCast) {
        Map map = get(key, Map.class);
        Map<KEY, VALUE> mapCasted = new HashMap<>();
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            mapCasted.put(CastUtils.cast(entry.getKey(), keyToCast), CastUtils.cast(entry.getValue(), valueToCast));
        }
        return mapCasted;
    }

    // GENERATE:MAP-SHORTCUTS
}
