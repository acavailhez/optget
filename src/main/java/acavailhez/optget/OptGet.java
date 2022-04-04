package acavailhez.optget;


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

        default String optString(Object key) {
        return opt(key, String.class);
    }

    default String optString(Object key, String defaultValue) {
        return opt(key, String.class, defaultValue);
    }

    default String getString(Object key) {
        return get(key, String.class);
    }

    default Byte optByte(Object key) {
        return opt(key, Byte.class);
    }

    default Byte optByte(Object key, Byte defaultValue) {
        return opt(key, Byte.class, defaultValue);
    }

    default Byte getByte(Object key) {
        return get(key, Byte.class);
    }

    default Short optShort(Object key) {
        return opt(key, Short.class);
    }

    default Short optShort(Object key, Short defaultValue) {
        return opt(key, Short.class, defaultValue);
    }

    default Short getShort(Object key) {
        return get(key, Short.class);
    }

    default Integer optInteger(Object key) {
        return opt(key, Integer.class);
    }

    default Integer optInteger(Object key, Integer defaultValue) {
        return opt(key, Integer.class, defaultValue);
    }

    default Integer getInteger(Object key) {
        return get(key, Integer.class);
    }

    default Long optLong(Object key) {
        return opt(key, Long.class);
    }

    default Long optLong(Object key, Long defaultValue) {
        return opt(key, Long.class, defaultValue);
    }

    default Long getLong(Object key) {
        return get(key, Long.class);
    }

    default Float optFloat(Object key) {
        return opt(key, Float.class);
    }

    default Float optFloat(Object key, Float defaultValue) {
        return opt(key, Float.class, defaultValue);
    }

    default Float getFloat(Object key) {
        return get(key, Float.class);
    }

    default Double optDouble(Object key) {
        return opt(key, Double.class);
    }

    default Double optDouble(Object key, Double defaultValue) {
        return opt(key, Double.class, defaultValue);
    }

    default Double getDouble(Object key) {
        return get(key, Double.class);
    }

    default OptGet optOptGet(Object key) {
        return opt(key, OptGet.class);
    }

    default OptGet optOptGet(Object key, OptGet defaultValue) {
        return opt(key, OptGet.class, defaultValue);
    }

    default OptGet getOptGet(Object key) {
        return get(key, OptGet.class);
    }

    default OptGetMap optOptGetMap(Object key) {
        return opt(key, OptGetMap.class);
    }

    default OptGetMap optOptGetMap(Object key, OptGetMap defaultValue) {
        return opt(key, OptGetMap.class, defaultValue);
    }

    default OptGetMap getOptGetMap(Object key) {
        return get(key, OptGetMap.class);
    }



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

        default List<String> optListString(Object key) {
        return optList(key, String.class);
    }

    default List<String> getListString(Object key) {
        return getList(key, String.class);
    }

    default List<Byte> optListByte(Object key) {
        return optList(key, Byte.class);
    }

    default List<Byte> getListByte(Object key) {
        return getList(key, Byte.class);
    }

    default List<Short> optListShort(Object key) {
        return optList(key, Short.class);
    }

    default List<Short> getListShort(Object key) {
        return getList(key, Short.class);
    }

    default List<Integer> optListInteger(Object key) {
        return optList(key, Integer.class);
    }

    default List<Integer> getListInteger(Object key) {
        return getList(key, Integer.class);
    }

    default List<Long> optListLong(Object key) {
        return optList(key, Long.class);
    }

    default List<Long> getListLong(Object key) {
        return getList(key, Long.class);
    }

    default List<Float> optListFloat(Object key) {
        return optList(key, Float.class);
    }

    default List<Float> getListFloat(Object key) {
        return getList(key, Float.class);
    }

    default List<Double> optListDouble(Object key) {
        return optList(key, Double.class);
    }

    default List<Double> getListDouble(Object key) {
        return getList(key, Double.class);
    }

    default List<OptGet> optListOptGet(Object key) {
        return optList(key, OptGet.class);
    }

    default List<OptGet> getListOptGet(Object key) {
        return getList(key, OptGet.class);
    }

    default List<OptGetMap> optListOptGetMap(Object key) {
        return optList(key, OptGetMap.class);
    }

    default List<OptGetMap> getListOptGetMap(Object key) {
        return getList(key, OptGetMap.class);
    }



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

        default Map<String, String> optMapStringString(Object key) {
        return optMap(key, String.class, String.class);
    }

    default Map<String, String> getMapStringString(Object key) {
        return getMap(key, String.class, String.class);
    }

    default Map<String, Byte> optMapStringByte(Object key) {
        return optMap(key, String.class, Byte.class);
    }

    default Map<String, Byte> getMapStringByte(Object key) {
        return getMap(key, String.class, Byte.class);
    }

    default Map<String, Short> optMapStringShort(Object key) {
        return optMap(key, String.class, Short.class);
    }

    default Map<String, Short> getMapStringShort(Object key) {
        return getMap(key, String.class, Short.class);
    }

    default Map<String, Integer> optMapStringInteger(Object key) {
        return optMap(key, String.class, Integer.class);
    }

    default Map<String, Integer> getMapStringInteger(Object key) {
        return getMap(key, String.class, Integer.class);
    }

    default Map<String, Long> optMapStringLong(Object key) {
        return optMap(key, String.class, Long.class);
    }

    default Map<String, Long> getMapStringLong(Object key) {
        return getMap(key, String.class, Long.class);
    }

    default Map<String, Float> optMapStringFloat(Object key) {
        return optMap(key, String.class, Float.class);
    }

    default Map<String, Float> getMapStringFloat(Object key) {
        return getMap(key, String.class, Float.class);
    }

    default Map<String, Double> optMapStringDouble(Object key) {
        return optMap(key, String.class, Double.class);
    }

    default Map<String, Double> getMapStringDouble(Object key) {
        return getMap(key, String.class, Double.class);
    }

    default Map<String, OptGet> optMapStringOptGet(Object key) {
        return optMap(key, String.class, OptGet.class);
    }

    default Map<String, OptGet> getMapStringOptGet(Object key) {
        return getMap(key, String.class, OptGet.class);
    }

    default Map<String, OptGetMap> optMapStringOptGetMap(Object key) {
        return optMap(key, String.class, OptGetMap.class);
    }

    default Map<String, OptGetMap> getMapStringOptGetMap(Object key) {
        return getMap(key, String.class, OptGetMap.class);
    }

    default Map<Integer, String> optMapIntegerString(Object key) {
        return optMap(key, Integer.class, String.class);
    }

    default Map<Integer, String> getMapIntegerString(Object key) {
        return getMap(key, Integer.class, String.class);
    }

    default Map<Integer, Byte> optMapIntegerByte(Object key) {
        return optMap(key, Integer.class, Byte.class);
    }

    default Map<Integer, Byte> getMapIntegerByte(Object key) {
        return getMap(key, Integer.class, Byte.class);
    }

    default Map<Integer, Short> optMapIntegerShort(Object key) {
        return optMap(key, Integer.class, Short.class);
    }

    default Map<Integer, Short> getMapIntegerShort(Object key) {
        return getMap(key, Integer.class, Short.class);
    }

    default Map<Integer, Integer> optMapIntegerInteger(Object key) {
        return optMap(key, Integer.class, Integer.class);
    }

    default Map<Integer, Integer> getMapIntegerInteger(Object key) {
        return getMap(key, Integer.class, Integer.class);
    }

    default Map<Integer, Long> optMapIntegerLong(Object key) {
        return optMap(key, Integer.class, Long.class);
    }

    default Map<Integer, Long> getMapIntegerLong(Object key) {
        return getMap(key, Integer.class, Long.class);
    }

    default Map<Integer, Float> optMapIntegerFloat(Object key) {
        return optMap(key, Integer.class, Float.class);
    }

    default Map<Integer, Float> getMapIntegerFloat(Object key) {
        return getMap(key, Integer.class, Float.class);
    }

    default Map<Integer, Double> optMapIntegerDouble(Object key) {
        return optMap(key, Integer.class, Double.class);
    }

    default Map<Integer, Double> getMapIntegerDouble(Object key) {
        return getMap(key, Integer.class, Double.class);
    }

    default Map<Integer, OptGet> optMapIntegerOptGet(Object key) {
        return optMap(key, Integer.class, OptGet.class);
    }

    default Map<Integer, OptGet> getMapIntegerOptGet(Object key) {
        return getMap(key, Integer.class, OptGet.class);
    }

    default Map<Integer, OptGetMap> optMapIntegerOptGetMap(Object key) {
        return optMap(key, Integer.class, OptGetMap.class);
    }

    default Map<Integer, OptGetMap> getMapIntegerOptGetMap(Object key) {
        return getMap(key, Integer.class, OptGetMap.class);
    }

    default Map<Long, String> optMapLongString(Object key) {
        return optMap(key, Long.class, String.class);
    }

    default Map<Long, String> getMapLongString(Object key) {
        return getMap(key, Long.class, String.class);
    }

    default Map<Long, Byte> optMapLongByte(Object key) {
        return optMap(key, Long.class, Byte.class);
    }

    default Map<Long, Byte> getMapLongByte(Object key) {
        return getMap(key, Long.class, Byte.class);
    }

    default Map<Long, Short> optMapLongShort(Object key) {
        return optMap(key, Long.class, Short.class);
    }

    default Map<Long, Short> getMapLongShort(Object key) {
        return getMap(key, Long.class, Short.class);
    }

    default Map<Long, Integer> optMapLongInteger(Object key) {
        return optMap(key, Long.class, Integer.class);
    }

    default Map<Long, Integer> getMapLongInteger(Object key) {
        return getMap(key, Long.class, Integer.class);
    }

    default Map<Long, Long> optMapLongLong(Object key) {
        return optMap(key, Long.class, Long.class);
    }

    default Map<Long, Long> getMapLongLong(Object key) {
        return getMap(key, Long.class, Long.class);
    }

    default Map<Long, Float> optMapLongFloat(Object key) {
        return optMap(key, Long.class, Float.class);
    }

    default Map<Long, Float> getMapLongFloat(Object key) {
        return getMap(key, Long.class, Float.class);
    }

    default Map<Long, Double> optMapLongDouble(Object key) {
        return optMap(key, Long.class, Double.class);
    }

    default Map<Long, Double> getMapLongDouble(Object key) {
        return getMap(key, Long.class, Double.class);
    }

    default Map<Long, OptGet> optMapLongOptGet(Object key) {
        return optMap(key, Long.class, OptGet.class);
    }

    default Map<Long, OptGet> getMapLongOptGet(Object key) {
        return getMap(key, Long.class, OptGet.class);
    }

    default Map<Long, OptGetMap> optMapLongOptGetMap(Object key) {
        return optMap(key, Long.class, OptGetMap.class);
    }

    default Map<Long, OptGetMap> getMapLongOptGetMap(Object key) {
        return getMap(key, Long.class, OptGetMap.class);
    }

    default Map<Float, String> optMapFloatString(Object key) {
        return optMap(key, Float.class, String.class);
    }

    default Map<Float, String> getMapFloatString(Object key) {
        return getMap(key, Float.class, String.class);
    }

    default Map<Float, Byte> optMapFloatByte(Object key) {
        return optMap(key, Float.class, Byte.class);
    }

    default Map<Float, Byte> getMapFloatByte(Object key) {
        return getMap(key, Float.class, Byte.class);
    }

    default Map<Float, Short> optMapFloatShort(Object key) {
        return optMap(key, Float.class, Short.class);
    }

    default Map<Float, Short> getMapFloatShort(Object key) {
        return getMap(key, Float.class, Short.class);
    }

    default Map<Float, Integer> optMapFloatInteger(Object key) {
        return optMap(key, Float.class, Integer.class);
    }

    default Map<Float, Integer> getMapFloatInteger(Object key) {
        return getMap(key, Float.class, Integer.class);
    }

    default Map<Float, Long> optMapFloatLong(Object key) {
        return optMap(key, Float.class, Long.class);
    }

    default Map<Float, Long> getMapFloatLong(Object key) {
        return getMap(key, Float.class, Long.class);
    }

    default Map<Float, Float> optMapFloatFloat(Object key) {
        return optMap(key, Float.class, Float.class);
    }

    default Map<Float, Float> getMapFloatFloat(Object key) {
        return getMap(key, Float.class, Float.class);
    }

    default Map<Float, Double> optMapFloatDouble(Object key) {
        return optMap(key, Float.class, Double.class);
    }

    default Map<Float, Double> getMapFloatDouble(Object key) {
        return getMap(key, Float.class, Double.class);
    }

    default Map<Float, OptGet> optMapFloatOptGet(Object key) {
        return optMap(key, Float.class, OptGet.class);
    }

    default Map<Float, OptGet> getMapFloatOptGet(Object key) {
        return getMap(key, Float.class, OptGet.class);
    }

    default Map<Float, OptGetMap> optMapFloatOptGetMap(Object key) {
        return optMap(key, Float.class, OptGetMap.class);
    }

    default Map<Float, OptGetMap> getMapFloatOptGetMap(Object key) {
        return getMap(key, Float.class, OptGetMap.class);
    }

    default Map<Double, String> optMapDoubleString(Object key) {
        return optMap(key, Double.class, String.class);
    }

    default Map<Double, String> getMapDoubleString(Object key) {
        return getMap(key, Double.class, String.class);
    }

    default Map<Double, Byte> optMapDoubleByte(Object key) {
        return optMap(key, Double.class, Byte.class);
    }

    default Map<Double, Byte> getMapDoubleByte(Object key) {
        return getMap(key, Double.class, Byte.class);
    }

    default Map<Double, Short> optMapDoubleShort(Object key) {
        return optMap(key, Double.class, Short.class);
    }

    default Map<Double, Short> getMapDoubleShort(Object key) {
        return getMap(key, Double.class, Short.class);
    }

    default Map<Double, Integer> optMapDoubleInteger(Object key) {
        return optMap(key, Double.class, Integer.class);
    }

    default Map<Double, Integer> getMapDoubleInteger(Object key) {
        return getMap(key, Double.class, Integer.class);
    }

    default Map<Double, Long> optMapDoubleLong(Object key) {
        return optMap(key, Double.class, Long.class);
    }

    default Map<Double, Long> getMapDoubleLong(Object key) {
        return getMap(key, Double.class, Long.class);
    }

    default Map<Double, Float> optMapDoubleFloat(Object key) {
        return optMap(key, Double.class, Float.class);
    }

    default Map<Double, Float> getMapDoubleFloat(Object key) {
        return getMap(key, Double.class, Float.class);
    }

    default Map<Double, Double> optMapDoubleDouble(Object key) {
        return optMap(key, Double.class, Double.class);
    }

    default Map<Double, Double> getMapDoubleDouble(Object key) {
        return getMap(key, Double.class, Double.class);
    }

    default Map<Double, OptGet> optMapDoubleOptGet(Object key) {
        return optMap(key, Double.class, OptGet.class);
    }

    default Map<Double, OptGet> getMapDoubleOptGet(Object key) {
        return getMap(key, Double.class, OptGet.class);
    }

    default Map<Double, OptGetMap> optMapDoubleOptGetMap(Object key) {
        return optMap(key, Double.class, OptGetMap.class);
    }

    default Map<Double, OptGetMap> getMapDoubleOptGetMap(Object key) {
        return getMap(key, Double.class, OptGetMap.class);
    }


}
