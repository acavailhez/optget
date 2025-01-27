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

    // GENERATED-BEGIN:SIMPLE-SHORTCUTS     String optString(Object key) {
        return opt(key, String.class);
    }

     String optString(Object key, String defaultValue) {
        return opt(key, String.class, defaultValue);
    }

     String getString(Object key) {
        return get(key, String.class);
    }

     Byte optByte(Object key) {
        return opt(key, Byte.class);
    }

     Byte optByte(Object key, Byte defaultValue) {
        return opt(key, Byte.class, defaultValue);
    }

     Byte getByte(Object key) {
        return get(key, Byte.class);
    }

     Short optShort(Object key) {
        return opt(key, Short.class);
    }

     Short optShort(Object key, Short defaultValue) {
        return opt(key, Short.class, defaultValue);
    }

     Short getShort(Object key) {
        return get(key, Short.class);
    }

     Integer optInteger(Object key) {
        return opt(key, Integer.class);
    }

     Integer optInteger(Object key, Integer defaultValue) {
        return opt(key, Integer.class, defaultValue);
    }

     Integer getInteger(Object key) {
        return get(key, Integer.class);
    }

     Long optLong(Object key) {
        return opt(key, Long.class);
    }

     Long optLong(Object key, Long defaultValue) {
        return opt(key, Long.class, defaultValue);
    }

     Long getLong(Object key) {
        return get(key, Long.class);
    }

     Float optFloat(Object key) {
        return opt(key, Float.class);
    }

     Float optFloat(Object key, Float defaultValue) {
        return opt(key, Float.class, defaultValue);
    }

     Float getFloat(Object key) {
        return get(key, Float.class);
    }

     Double optDouble(Object key) {
        return opt(key, Double.class);
    }

     Double optDouble(Object key, Double defaultValue) {
        return opt(key, Double.class, defaultValue);
    }

     Double getDouble(Object key) {
        return get(key, Double.class);
    }

     OptGet optOptGet(Object key) {
        return opt(key, OptGet.class);
    }

     OptGet optOptGet(Object key, OptGet defaultValue) {
        return opt(key, OptGet.class, defaultValue);
    }

     OptGet getOptGet(Object key) {
        return get(key, OptGet.class);
    }

     OptGetMap optOptGetMap(Object key) {
        return opt(key, OptGetMap.class);
    }

     OptGetMap optOptGetMap(Object key, OptGetMap defaultValue) {
        return opt(key, OptGetMap.class, defaultValue);
    }

     OptGetMap getOptGetMap(Object key) {
        return get(key, OptGetMap.class);
    }


    // GENERATED-END:SIMPLE-SHORTCUTS

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

    // GENERATED-BEGIN:LIST-SHORTCUTS     List<String> optListString(Object key) {
        return optList(key, String.class);
    }

     List<String> getListString(Object key) {
        return getList(key, String.class);
    }

     List<Byte> optListByte(Object key) {
        return optList(key, Byte.class);
    }

     List<Byte> getListByte(Object key) {
        return getList(key, Byte.class);
    }

     List<Short> optListShort(Object key) {
        return optList(key, Short.class);
    }

     List<Short> getListShort(Object key) {
        return getList(key, Short.class);
    }

     List<Integer> optListInteger(Object key) {
        return optList(key, Integer.class);
    }

     List<Integer> getListInteger(Object key) {
        return getList(key, Integer.class);
    }

     List<Long> optListLong(Object key) {
        return optList(key, Long.class);
    }

     List<Long> getListLong(Object key) {
        return getList(key, Long.class);
    }

     List<Float> optListFloat(Object key) {
        return optList(key, Float.class);
    }

     List<Float> getListFloat(Object key) {
        return getList(key, Float.class);
    }

     List<Double> optListDouble(Object key) {
        return optList(key, Double.class);
    }

     List<Double> getListDouble(Object key) {
        return getList(key, Double.class);
    }

     List<OptGet> optListOptGet(Object key) {
        return optList(key, OptGet.class);
    }

     List<OptGet> getListOptGet(Object key) {
        return getList(key, OptGet.class);
    }

     List<OptGetMap> optListOptGetMap(Object key) {
        return optList(key, OptGetMap.class);
    }

     List<OptGetMap> getListOptGetMap(Object key) {
        return getList(key, OptGetMap.class);
    }


    // GENERATED-END:LIST-SHORTCUTS

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

    // GENERATED-BEGIN:MAP-SHORTCUTS     Map<String, String> optMapStringString(Object key) {
        return optMap(key, String.class, String.class);
    }

     Map<String, String> getMapStringString(Object key) {
        return getMap(key, String.class, String.class);
    }

     Map<String, Byte> optMapStringByte(Object key) {
        return optMap(key, String.class, Byte.class);
    }

     Map<String, Byte> getMapStringByte(Object key) {
        return getMap(key, String.class, Byte.class);
    }

     Map<String, Short> optMapStringShort(Object key) {
        return optMap(key, String.class, Short.class);
    }

     Map<String, Short> getMapStringShort(Object key) {
        return getMap(key, String.class, Short.class);
    }

     Map<String, Integer> optMapStringInteger(Object key) {
        return optMap(key, String.class, Integer.class);
    }

     Map<String, Integer> getMapStringInteger(Object key) {
        return getMap(key, String.class, Integer.class);
    }

     Map<String, Long> optMapStringLong(Object key) {
        return optMap(key, String.class, Long.class);
    }

     Map<String, Long> getMapStringLong(Object key) {
        return getMap(key, String.class, Long.class);
    }

     Map<String, Float> optMapStringFloat(Object key) {
        return optMap(key, String.class, Float.class);
    }

     Map<String, Float> getMapStringFloat(Object key) {
        return getMap(key, String.class, Float.class);
    }

     Map<String, Double> optMapStringDouble(Object key) {
        return optMap(key, String.class, Double.class);
    }

     Map<String, Double> getMapStringDouble(Object key) {
        return getMap(key, String.class, Double.class);
    }

     Map<String, OptGet> optMapStringOptGet(Object key) {
        return optMap(key, String.class, OptGet.class);
    }

     Map<String, OptGet> getMapStringOptGet(Object key) {
        return getMap(key, String.class, OptGet.class);
    }

     Map<String, OptGetMap> optMapStringOptGetMap(Object key) {
        return optMap(key, String.class, OptGetMap.class);
    }

     Map<String, OptGetMap> getMapStringOptGetMap(Object key) {
        return getMap(key, String.class, OptGetMap.class);
    }

     Map<Integer, String> optMapIntegerString(Object key) {
        return optMap(key, Integer.class, String.class);
    }

     Map<Integer, String> getMapIntegerString(Object key) {
        return getMap(key, Integer.class, String.class);
    }

     Map<Integer, Byte> optMapIntegerByte(Object key) {
        return optMap(key, Integer.class, Byte.class);
    }

     Map<Integer, Byte> getMapIntegerByte(Object key) {
        return getMap(key, Integer.class, Byte.class);
    }

     Map<Integer, Short> optMapIntegerShort(Object key) {
        return optMap(key, Integer.class, Short.class);
    }

     Map<Integer, Short> getMapIntegerShort(Object key) {
        return getMap(key, Integer.class, Short.class);
    }

     Map<Integer, Integer> optMapIntegerInteger(Object key) {
        return optMap(key, Integer.class, Integer.class);
    }

     Map<Integer, Integer> getMapIntegerInteger(Object key) {
        return getMap(key, Integer.class, Integer.class);
    }

     Map<Integer, Long> optMapIntegerLong(Object key) {
        return optMap(key, Integer.class, Long.class);
    }

     Map<Integer, Long> getMapIntegerLong(Object key) {
        return getMap(key, Integer.class, Long.class);
    }

     Map<Integer, Float> optMapIntegerFloat(Object key) {
        return optMap(key, Integer.class, Float.class);
    }

     Map<Integer, Float> getMapIntegerFloat(Object key) {
        return getMap(key, Integer.class, Float.class);
    }

     Map<Integer, Double> optMapIntegerDouble(Object key) {
        return optMap(key, Integer.class, Double.class);
    }

     Map<Integer, Double> getMapIntegerDouble(Object key) {
        return getMap(key, Integer.class, Double.class);
    }

     Map<Integer, OptGet> optMapIntegerOptGet(Object key) {
        return optMap(key, Integer.class, OptGet.class);
    }

     Map<Integer, OptGet> getMapIntegerOptGet(Object key) {
        return getMap(key, Integer.class, OptGet.class);
    }

     Map<Integer, OptGetMap> optMapIntegerOptGetMap(Object key) {
        return optMap(key, Integer.class, OptGetMap.class);
    }

     Map<Integer, OptGetMap> getMapIntegerOptGetMap(Object key) {
        return getMap(key, Integer.class, OptGetMap.class);
    }

     Map<Long, String> optMapLongString(Object key) {
        return optMap(key, Long.class, String.class);
    }

     Map<Long, String> getMapLongString(Object key) {
        return getMap(key, Long.class, String.class);
    }

     Map<Long, Byte> optMapLongByte(Object key) {
        return optMap(key, Long.class, Byte.class);
    }

     Map<Long, Byte> getMapLongByte(Object key) {
        return getMap(key, Long.class, Byte.class);
    }

     Map<Long, Short> optMapLongShort(Object key) {
        return optMap(key, Long.class, Short.class);
    }

     Map<Long, Short> getMapLongShort(Object key) {
        return getMap(key, Long.class, Short.class);
    }

     Map<Long, Integer> optMapLongInteger(Object key) {
        return optMap(key, Long.class, Integer.class);
    }

     Map<Long, Integer> getMapLongInteger(Object key) {
        return getMap(key, Long.class, Integer.class);
    }

     Map<Long, Long> optMapLongLong(Object key) {
        return optMap(key, Long.class, Long.class);
    }

     Map<Long, Long> getMapLongLong(Object key) {
        return getMap(key, Long.class, Long.class);
    }

     Map<Long, Float> optMapLongFloat(Object key) {
        return optMap(key, Long.class, Float.class);
    }

     Map<Long, Float> getMapLongFloat(Object key) {
        return getMap(key, Long.class, Float.class);
    }

     Map<Long, Double> optMapLongDouble(Object key) {
        return optMap(key, Long.class, Double.class);
    }

     Map<Long, Double> getMapLongDouble(Object key) {
        return getMap(key, Long.class, Double.class);
    }

     Map<Long, OptGet> optMapLongOptGet(Object key) {
        return optMap(key, Long.class, OptGet.class);
    }

     Map<Long, OptGet> getMapLongOptGet(Object key) {
        return getMap(key, Long.class, OptGet.class);
    }

     Map<Long, OptGetMap> optMapLongOptGetMap(Object key) {
        return optMap(key, Long.class, OptGetMap.class);
    }

     Map<Long, OptGetMap> getMapLongOptGetMap(Object key) {
        return getMap(key, Long.class, OptGetMap.class);
    }

     Map<Float, String> optMapFloatString(Object key) {
        return optMap(key, Float.class, String.class);
    }

     Map<Float, String> getMapFloatString(Object key) {
        return getMap(key, Float.class, String.class);
    }

     Map<Float, Byte> optMapFloatByte(Object key) {
        return optMap(key, Float.class, Byte.class);
    }

     Map<Float, Byte> getMapFloatByte(Object key) {
        return getMap(key, Float.class, Byte.class);
    }

     Map<Float, Short> optMapFloatShort(Object key) {
        return optMap(key, Float.class, Short.class);
    }

     Map<Float, Short> getMapFloatShort(Object key) {
        return getMap(key, Float.class, Short.class);
    }

     Map<Float, Integer> optMapFloatInteger(Object key) {
        return optMap(key, Float.class, Integer.class);
    }

     Map<Float, Integer> getMapFloatInteger(Object key) {
        return getMap(key, Float.class, Integer.class);
    }

     Map<Float, Long> optMapFloatLong(Object key) {
        return optMap(key, Float.class, Long.class);
    }

     Map<Float, Long> getMapFloatLong(Object key) {
        return getMap(key, Float.class, Long.class);
    }

     Map<Float, Float> optMapFloatFloat(Object key) {
        return optMap(key, Float.class, Float.class);
    }

     Map<Float, Float> getMapFloatFloat(Object key) {
        return getMap(key, Float.class, Float.class);
    }

     Map<Float, Double> optMapFloatDouble(Object key) {
        return optMap(key, Float.class, Double.class);
    }

     Map<Float, Double> getMapFloatDouble(Object key) {
        return getMap(key, Float.class, Double.class);
    }

     Map<Float, OptGet> optMapFloatOptGet(Object key) {
        return optMap(key, Float.class, OptGet.class);
    }

     Map<Float, OptGet> getMapFloatOptGet(Object key) {
        return getMap(key, Float.class, OptGet.class);
    }

     Map<Float, OptGetMap> optMapFloatOptGetMap(Object key) {
        return optMap(key, Float.class, OptGetMap.class);
    }

     Map<Float, OptGetMap> getMapFloatOptGetMap(Object key) {
        return getMap(key, Float.class, OptGetMap.class);
    }

     Map<Double, String> optMapDoubleString(Object key) {
        return optMap(key, Double.class, String.class);
    }

     Map<Double, String> getMapDoubleString(Object key) {
        return getMap(key, Double.class, String.class);
    }

     Map<Double, Byte> optMapDoubleByte(Object key) {
        return optMap(key, Double.class, Byte.class);
    }

     Map<Double, Byte> getMapDoubleByte(Object key) {
        return getMap(key, Double.class, Byte.class);
    }

     Map<Double, Short> optMapDoubleShort(Object key) {
        return optMap(key, Double.class, Short.class);
    }

     Map<Double, Short> getMapDoubleShort(Object key) {
        return getMap(key, Double.class, Short.class);
    }

     Map<Double, Integer> optMapDoubleInteger(Object key) {
        return optMap(key, Double.class, Integer.class);
    }

     Map<Double, Integer> getMapDoubleInteger(Object key) {
        return getMap(key, Double.class, Integer.class);
    }

     Map<Double, Long> optMapDoubleLong(Object key) {
        return optMap(key, Double.class, Long.class);
    }

     Map<Double, Long> getMapDoubleLong(Object key) {
        return getMap(key, Double.class, Long.class);
    }

     Map<Double, Float> optMapDoubleFloat(Object key) {
        return optMap(key, Double.class, Float.class);
    }

     Map<Double, Float> getMapDoubleFloat(Object key) {
        return getMap(key, Double.class, Float.class);
    }

     Map<Double, Double> optMapDoubleDouble(Object key) {
        return optMap(key, Double.class, Double.class);
    }

     Map<Double, Double> getMapDoubleDouble(Object key) {
        return getMap(key, Double.class, Double.class);
    }

     Map<Double, OptGet> optMapDoubleOptGet(Object key) {
        return optMap(key, Double.class, OptGet.class);
    }

     Map<Double, OptGet> getMapDoubleOptGet(Object key) {
        return getMap(key, Double.class, OptGet.class);
    }

     Map<Double, OptGetMap> optMapDoubleOptGetMap(Object key) {
        return optMap(key, Double.class, OptGetMap.class);
    }

     Map<Double, OptGetMap> getMapDoubleOptGetMap(Object key) {
        return getMap(key, Double.class, OptGetMap.class);
    }


    // GENERATED-END:MAP-SHORTCUTS
}