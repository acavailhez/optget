// GENERATE:IMPORTS

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// OptGet wraps an object that only answers to a `public Object opt(Object key) throws Exception` method
// and exposes many shortcut functions to cast objects in desirable formats
public interface OptGet {

    // The main function to override
    // Lookup an object for a simple key (ie "location" instead of "location.latittude")
    // Can return null
    Object internalOpt(String key);

    // What to do when a key is missing
    // Typically, throw an IllegalArgumentException
    // but your application might need something else
    default void onMissingKey(String key, Class classToCast) {
        throw new IllegalArgumentException("Missing key:" + key + " of class:" + classToCast.getName());
    }

    default void onNullKey(String key, Class classToCast) {
        throw new IllegalArgumentException("Key:" + key + " of class:" + classToCast.getName()+ " is null");
    }


    // #####################
    //  OptGet basics
    // #####################

    default Object opt(Object key) {
        return opt(key, Object.class, null);
    }

    default <T> T opt(Object key, Class<T> classToCast) {
        return opt(key, classToCast, null);
    }

    default Object opt(Object key, Object defaultValue) {
        return opt(key, Object.class, defaultValue);
    }

    @SuppressWarnings("unchecked")
    default <T> T opt(Object key, Class<T> classToCast, T defaultValue) {
        Object nonCast = recursiveOpt(key);
        if (nonCast == null) {
            return defaultValue;
        }
        try {
            return CastUtils.cast(nonCast, classToCast);
        } catch (Throwable t) {
            throw new RuntimeException("Cannot read key " + key, t);
        }
    }

    default Object get(Object key) {
        return get(key, Object.class);
    }

    default <T> T get(Object key, Class<T> classToCast) {
        T value = opt(key, classToCast);
        if (value == null) {
            onNullKey(key.toString(), classToCast);
        }
        return value;
    }

    // #####################
    //  Internal Utils
    // #####################

    default Object opt(String key){
        return recursiveOpt(key);
    }

    // Will transform getString("key.sub") to getGetOpt("key").getString("sub")
    // when used in groovy, map.key.sub will then work
    default Object recursiveOpt(Object key) {
        Object value = internalOpt(key.toString());
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
        return optGet.internalOpt(subkeys[subkeys.length - 1]);
    }


    // #####################
    //  Shortcuts
    // #####################

    // Simple shortcuts

    // GENERATE:SIMPLE-SHORTCUTS

    default <ENUM extends Enum> ENUM optEnum(Object key, Class<ENUM> enumClass) {
        return opt(key, enumClass);
    }

    default <ENUM extends Enum> ENUM optEnum(Object key, Class<ENUM> enumClass, ENUM defaultValue) {
        return opt(key, enumClass, defaultValue);
    }

    default <ENUM extends Enum> ENUM getEnum(Object key, Class<ENUM> enumClass) {
        return get(key, enumClass);
    }

    // List shortcuts

    default List optList(Object key) {
        return opt(key, List.class);
    }

    default List getList(Object key) {
        return get(key, List.class);
    }

    default <T> List<T> optList(Object key, Class<T> classToCast) {
        List list = opt(key, List.class);
        List<T> listCasted = new LinkedList<T>();
        for (Object o : list) {
            listCasted.add(CastUtils.cast(o, classToCast));
        }
        return listCasted;
    }

    default <T> List<T> getList(Object key, Class<T> classToCast) {
        List<T> value = optList(key, classToCast);
        if (value == null) {
            onNullKey(key.toString(), List.class);
        }
        return value;
    }

    // GENERATE:LIST-SHORTCUTS

    default <ENUM extends Enum> List<ENUM> optListEnum(Object key, Class<ENUM> enumClass) {
        return optList(key, enumClass);
    }

    default <ENUM extends Enum> List<ENUM> getListEnum(Object key, Class<ENUM> enumClass) {
        return getList(key, enumClass);
    }

    // Map shortcuts

    default Map optMap(Object key) {
        return opt(key, Map.class);
    }

    default Map getMap(Object key) {
        return get(key, Map.class);
    }

    default <KEY, VALUE> Map<KEY, VALUE> optMap(Object key, Class<KEY> keyToCast, Class<VALUE> valueToCast) {
        Map map = opt(key, Map.class);
        Map<KEY, VALUE> mapCasted = new HashMap<>();
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            mapCasted.put(CastUtils.cast(entry.getKey(), keyToCast), CastUtils.cast(entry.getValue(), valueToCast));
        }
        return mapCasted;
    }

    default <KEY, VALUE> Map<KEY, VALUE> getMap(Object key, Class<KEY> keyToCast, Class<VALUE> valueToCast) {
        Map<KEY, VALUE> mapCasted = optMap(key, keyToCast, valueToCast);
        if (mapCasted == null) {
            onNullKey(key.toString(), Map.class);
        }
        return mapCasted;
    }

    // GENERATE:MAP-SHORTCUTS
}
