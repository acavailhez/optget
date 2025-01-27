package acavailhez.optget;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// acavailhez.optget.OptGet wraps an object that only answers to a `public Object opt(Object key) throws Exception` method
// and exposes many shortcut functions to cast objects in desirable formats
public abstract class OptGet {

    // #####################
    //  Functions to override
    // #####################

    // The main function to override
    // Lookup an object for a simple key (ie "location" instead of "location.latitude")
    // Can return null
    protected abstract @Nullable Object optToOverride(@NotNull String key);

    // What to do when a key is missing
    // Typically, throw an IllegalArgumentException
    // but your application might need something else
    protected <T> void onMissingKey(@NotNull String key, @NotNull Class<T> classToCast) {
        throw new IllegalArgumentException("Missing key:" + key + " of class:" + classToCast.getName());
    }

    // What to do when a key exists, but the value is missing
    // Typically, throw an IllegalArgumentException
    // but your application might need something else
    protected <T> void onNullValue(@NotNull String key, @NotNull Class<T> classToCast) {
        throw new IllegalArgumentException("Key:" + key + " of class:" + classToCast.getName() + " has null value");
    }

    // #####################
    //  Basics
    // #####################

    // Simplest opt
    @Nullable Object opt(@NotNull Object key) {
        return privateOpt(key, Object.class, null);
    }

    // opt with a cast
    <T> @Nullable T opt(@NotNull Object key, @NotNull Class<T> classToCast) {
        return privateOpt(key, classToCast, null);
    }

    // opt with a default value
    @Nullable Object opt(@NotNull Object key, @NotNull Object defaultValue) {
        return privateOpt(key, Object.class, defaultValue);
    }

    // opt with a cast and a default value
    <T> @NotNull T opt(@NotNull Object key, @NotNull Class<T> classToCast, @NotNull T defaultValue) {
        return Objects.requireNonNull(privateOpt(key, classToCast, defaultValue));
    }

    // simplest get
    @NotNull Object get(@NotNull Object key) {
        return get(key, Object.class);
    }

    // get with a cast
    <T> @NotNull T get(@NotNull Object key, @NotNull Class<T> classToCast) {
        T value = opt(key, classToCast);
        if (value == null) {
            onNullValue(key.toString(), classToCast);
        }
        return value;
    }

    // #####################
    //  Internals
    // #####################

    private <T> @Nullable T privateOpt(@NotNull Object key, @NotNull Class<T> classToCast, @Nullable T defaultValue) {
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

    // Will transform getString("key.sub") to getGetOpt("key").getString("sub")
    // when used in groovy, map.key.sub will then work
    @Nullable Object recursiveOpt(@NotNull Object key) {
        // First attempt to get the value directly
        Object value = optToOverride(key.toString());
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
        return optGet.optToOverride(subkeys[subkeys.length - 1]);
    }


    // #####################
    //  Shortcuts
    // #####################

    // Simple shortcuts

    // GENERATE:SIMPLE-SHORTCUTS

    <ENUM extends Enum> ENUM optEnum(Object key, Class<ENUM> enumClass) {
        return opt(key, enumClass);
    }

    <ENUM extends Enum> ENUM optEnum(Object key, Class<ENUM> enumClass, ENUM defaultValue) {
        return opt(key, enumClass, defaultValue);
    }

    <ENUM extends Enum> ENUM getEnum(Object key, Class<ENUM> enumClass) {
        return get(key, enumClass);
    }

    // List shortcuts

    List optList(Object key) {
        return opt(key, List.class);
    }

    List getList(Object key) {
        return get(key, List.class);
    }

    <T> List<T> optList(Object key, Class<T> classToCast) {
        List list = opt(key, List.class);
        List<T> listCasted = new LinkedList<T>();
        for (Object o : list) {
            listCasted.add(CastUtils.cast(o, classToCast));
        }
        return listCasted;
    }

    <T> List<T> getList(Object key, Class<T> classToCast) {
        List<T> value = optList(key, classToCast);
        if (value == null) {
            onNullValue(key.toString(), List.class);
        }
        return value;
    }

    // GENERATE:LIST-SHORTCUTS

    <ENUM extends Enum> List<ENUM> optListEnum(Object key, Class<ENUM> enumClass) {
        return optList(key, enumClass);
    }

    <ENUM extends Enum> List<ENUM> getListEnum(Object key, Class<ENUM> enumClass) {
        return getList(key, enumClass);
    }

    // Map shortcuts

    Map optMap(Object key) {
        return opt(key, Map.class);
    }

    Map getMap(Object key) {
        return get(key, Map.class);
    }

    <KEY, VALUE> Map<KEY, VALUE> optMap(Object key, Class<KEY> keyToCast, Class<VALUE> valueToCast) {
        Map map = opt(key, Map.class);
        Map<KEY, VALUE> mapCasted = new HashMap<>();
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            mapCasted.put(CastUtils.cast(entry.getKey(), keyToCast), CastUtils.cast(entry.getValue(), valueToCast));
        }
        return mapCasted;
    }

    <KEY, VALUE> Map<KEY, VALUE> getMap(Object key, Class<KEY> keyToCast, Class<VALUE> valueToCast) {
        Map<KEY, VALUE> mapCasted = optMap(key, keyToCast, valueToCast);
        if (mapCasted == null) {
            onNullValue(key.toString(), Map.class);
        }
        return mapCasted;
    }

    // GENERATE:MAP-SHORTCUTS
}
