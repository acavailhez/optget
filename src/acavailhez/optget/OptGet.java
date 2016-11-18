package acavailhez.optget;



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

    
    default String optString(String key) {
        return opt(key, String.class);
    }

    default String getString(String key) {
        return get(key, String.class);
    }

    default Byte optByte(String key) {
        return opt(key, Byte.class);
    }

    default Byte getByte(String key) {
        return get(key, Byte.class);
    }

    default Short optShort(String key) {
        return opt(key, Short.class);
    }

    default Short getShort(String key) {
        return get(key, Short.class);
    }

    default Integer optInteger(String key) {
        return opt(key, Integer.class);
    }

    default Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    default Long optLong(String key) {
        return opt(key, Long.class);
    }

    default Long getLong(String key) {
        return get(key, Long.class);
    }

    default Float optFloat(String key) {
        return opt(key, Float.class);
    }

    default Float getFloat(String key) {
        return get(key, Float.class);
    }

    default Double optDouble(String key) {
        return opt(key, Double.class);
    }

    default Double getDouble(String key) {
        return get(key, Double.class);
    }

    default OptGet optOptGet(String key) {
        return opt(key, OptGet.class);
    }

    default OptGet getOptGet(String key) {
        return get(key, OptGet.class);
    }


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

    
    default List<String> optListString(String key) {
        return optList(key, String.class);
    }

    default List<String> getListString(String key) {
        return getList(key, String.class);
    }

    default List<Byte> optListByte(String key) {
        return optList(key, Byte.class);
    }

    default List<Byte> getListByte(String key) {
        return getList(key, Byte.class);
    }

    default List<Short> optListShort(String key) {
        return optList(key, Short.class);
    }

    default List<Short> getListShort(String key) {
        return getList(key, Short.class);
    }

    default List<Integer> optListInteger(String key) {
        return optList(key, Integer.class);
    }

    default List<Integer> getListInteger(String key) {
        return getList(key, Integer.class);
    }

    default List<Long> optListLong(String key) {
        return optList(key, Long.class);
    }

    default List<Long> getListLong(String key) {
        return getList(key, Long.class);
    }

    default List<Float> optListFloat(String key) {
        return optList(key, Float.class);
    }

    default List<Float> getListFloat(String key) {
        return getList(key, Float.class);
    }

    default List<Double> optListDouble(String key) {
        return optList(key, Double.class);
    }

    default List<Double> getListDouble(String key) {
        return getList(key, Double.class);
    }

    default List<OptGet> optListOptGet(String key) {
        return optList(key, OptGet.class);
    }

    default List<OptGet> getListOptGet(String key) {
        return getList(key, OptGet.class);
    }


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

    
    default Map<String,String> optMapStringString(String key) {
        return optMap(key, String.class, String.class);
    }

    default Map<String,String> getMapStringString(String key) {
        return getMap(key, String.class, String.class);
    }

    default Map<String,Byte> optMapStringByte(String key) {
        return optMap(key, String.class, Byte.class);
    }

    default Map<String,Byte> getMapStringByte(String key) {
        return getMap(key, String.class, Byte.class);
    }

    default Map<String,Short> optMapStringShort(String key) {
        return optMap(key, String.class, Short.class);
    }

    default Map<String,Short> getMapStringShort(String key) {
        return getMap(key, String.class, Short.class);
    }

    default Map<String,Integer> optMapStringInteger(String key) {
        return optMap(key, String.class, Integer.class);
    }

    default Map<String,Integer> getMapStringInteger(String key) {
        return getMap(key, String.class, Integer.class);
    }

    default Map<String,Long> optMapStringLong(String key) {
        return optMap(key, String.class, Long.class);
    }

    default Map<String,Long> getMapStringLong(String key) {
        return getMap(key, String.class, Long.class);
    }

    default Map<String,Float> optMapStringFloat(String key) {
        return optMap(key, String.class, Float.class);
    }

    default Map<String,Float> getMapStringFloat(String key) {
        return getMap(key, String.class, Float.class);
    }

    default Map<String,Double> optMapStringDouble(String key) {
        return optMap(key, String.class, Double.class);
    }

    default Map<String,Double> getMapStringDouble(String key) {
        return getMap(key, String.class, Double.class);
    }

    default Map<String,OptGet> optMapStringOptGet(String key) {
        return optMap(key, String.class, OptGet.class);
    }

    default Map<String,OptGet> getMapStringOptGet(String key) {
        return getMap(key, String.class, OptGet.class);
    }

    default <ENUM extends Enum> Map<String,ENUM> optMapStringEnum(String key, Class<ENUM> enumClass) {
        return optMap(key, String.class, enumClass);
    }

    default <ENUM extends Enum> Map<String,ENUM>  getMapStringEnum(String key, Class<ENUM> enumClass) {
        return getMap(key, String.class, enumClass);
    }

    default Map<Integer,String> optMapIntegerString(String key) {
        return optMap(key, Integer.class, String.class);
    }

    default Map<Integer,String> getMapIntegerString(String key) {
        return getMap(key, Integer.class, String.class);
    }

    default Map<Integer,Byte> optMapIntegerByte(String key) {
        return optMap(key, Integer.class, Byte.class);
    }

    default Map<Integer,Byte> getMapIntegerByte(String key) {
        return getMap(key, Integer.class, Byte.class);
    }

    default Map<Integer,Short> optMapIntegerShort(String key) {
        return optMap(key, Integer.class, Short.class);
    }

    default Map<Integer,Short> getMapIntegerShort(String key) {
        return getMap(key, Integer.class, Short.class);
    }

    default Map<Integer,Integer> optMapIntegerInteger(String key) {
        return optMap(key, Integer.class, Integer.class);
    }

    default Map<Integer,Integer> getMapIntegerInteger(String key) {
        return getMap(key, Integer.class, Integer.class);
    }

    default Map<Integer,Long> optMapIntegerLong(String key) {
        return optMap(key, Integer.class, Long.class);
    }

    default Map<Integer,Long> getMapIntegerLong(String key) {
        return getMap(key, Integer.class, Long.class);
    }

    default Map<Integer,Float> optMapIntegerFloat(String key) {
        return optMap(key, Integer.class, Float.class);
    }

    default Map<Integer,Float> getMapIntegerFloat(String key) {
        return getMap(key, Integer.class, Float.class);
    }

    default Map<Integer,Double> optMapIntegerDouble(String key) {
        return optMap(key, Integer.class, Double.class);
    }

    default Map<Integer,Double> getMapIntegerDouble(String key) {
        return getMap(key, Integer.class, Double.class);
    }

    default Map<Integer,OptGet> optMapIntegerOptGet(String key) {
        return optMap(key, Integer.class, OptGet.class);
    }

    default Map<Integer,OptGet> getMapIntegerOptGet(String key) {
        return getMap(key, Integer.class, OptGet.class);
    }

    default <ENUM extends Enum> Map<Integer,ENUM> optMapIntegerEnum(String key, Class<ENUM> enumClass) {
        return optMap(key, Integer.class, enumClass);
    }

    default <ENUM extends Enum> Map<Integer,ENUM>  getMapIntegerEnum(String key, Class<ENUM> enumClass) {
        return getMap(key, Integer.class, enumClass);
    }

    default Map<Long,String> optMapLongString(String key) {
        return optMap(key, Long.class, String.class);
    }

    default Map<Long,String> getMapLongString(String key) {
        return getMap(key, Long.class, String.class);
    }

    default Map<Long,Byte> optMapLongByte(String key) {
        return optMap(key, Long.class, Byte.class);
    }

    default Map<Long,Byte> getMapLongByte(String key) {
        return getMap(key, Long.class, Byte.class);
    }

    default Map<Long,Short> optMapLongShort(String key) {
        return optMap(key, Long.class, Short.class);
    }

    default Map<Long,Short> getMapLongShort(String key) {
        return getMap(key, Long.class, Short.class);
    }

    default Map<Long,Integer> optMapLongInteger(String key) {
        return optMap(key, Long.class, Integer.class);
    }

    default Map<Long,Integer> getMapLongInteger(String key) {
        return getMap(key, Long.class, Integer.class);
    }

    default Map<Long,Long> optMapLongLong(String key) {
        return optMap(key, Long.class, Long.class);
    }

    default Map<Long,Long> getMapLongLong(String key) {
        return getMap(key, Long.class, Long.class);
    }

    default Map<Long,Float> optMapLongFloat(String key) {
        return optMap(key, Long.class, Float.class);
    }

    default Map<Long,Float> getMapLongFloat(String key) {
        return getMap(key, Long.class, Float.class);
    }

    default Map<Long,Double> optMapLongDouble(String key) {
        return optMap(key, Long.class, Double.class);
    }

    default Map<Long,Double> getMapLongDouble(String key) {
        return getMap(key, Long.class, Double.class);
    }

    default Map<Long,OptGet> optMapLongOptGet(String key) {
        return optMap(key, Long.class, OptGet.class);
    }

    default Map<Long,OptGet> getMapLongOptGet(String key) {
        return getMap(key, Long.class, OptGet.class);
    }

    default <ENUM extends Enum> Map<Long,ENUM> optMapLongEnum(String key, Class<ENUM> enumClass) {
        return optMap(key, Long.class, enumClass);
    }

    default <ENUM extends Enum> Map<Long,ENUM>  getMapLongEnum(String key, Class<ENUM> enumClass) {
        return getMap(key, Long.class, enumClass);
    }

    default Map<Float,String> optMapFloatString(String key) {
        return optMap(key, Float.class, String.class);
    }

    default Map<Float,String> getMapFloatString(String key) {
        return getMap(key, Float.class, String.class);
    }

    default Map<Float,Byte> optMapFloatByte(String key) {
        return optMap(key, Float.class, Byte.class);
    }

    default Map<Float,Byte> getMapFloatByte(String key) {
        return getMap(key, Float.class, Byte.class);
    }

    default Map<Float,Short> optMapFloatShort(String key) {
        return optMap(key, Float.class, Short.class);
    }

    default Map<Float,Short> getMapFloatShort(String key) {
        return getMap(key, Float.class, Short.class);
    }

    default Map<Float,Integer> optMapFloatInteger(String key) {
        return optMap(key, Float.class, Integer.class);
    }

    default Map<Float,Integer> getMapFloatInteger(String key) {
        return getMap(key, Float.class, Integer.class);
    }

    default Map<Float,Long> optMapFloatLong(String key) {
        return optMap(key, Float.class, Long.class);
    }

    default Map<Float,Long> getMapFloatLong(String key) {
        return getMap(key, Float.class, Long.class);
    }

    default Map<Float,Float> optMapFloatFloat(String key) {
        return optMap(key, Float.class, Float.class);
    }

    default Map<Float,Float> getMapFloatFloat(String key) {
        return getMap(key, Float.class, Float.class);
    }

    default Map<Float,Double> optMapFloatDouble(String key) {
        return optMap(key, Float.class, Double.class);
    }

    default Map<Float,Double> getMapFloatDouble(String key) {
        return getMap(key, Float.class, Double.class);
    }

    default Map<Float,OptGet> optMapFloatOptGet(String key) {
        return optMap(key, Float.class, OptGet.class);
    }

    default Map<Float,OptGet> getMapFloatOptGet(String key) {
        return getMap(key, Float.class, OptGet.class);
    }

    default <ENUM extends Enum> Map<Float,ENUM> optMapFloatEnum(String key, Class<ENUM> enumClass) {
        return optMap(key, Float.class, enumClass);
    }

    default <ENUM extends Enum> Map<Float,ENUM>  getMapFloatEnum(String key, Class<ENUM> enumClass) {
        return getMap(key, Float.class, enumClass);
    }

    default Map<Double,String> optMapDoubleString(String key) {
        return optMap(key, Double.class, String.class);
    }

    default Map<Double,String> getMapDoubleString(String key) {
        return getMap(key, Double.class, String.class);
    }

    default Map<Double,Byte> optMapDoubleByte(String key) {
        return optMap(key, Double.class, Byte.class);
    }

    default Map<Double,Byte> getMapDoubleByte(String key) {
        return getMap(key, Double.class, Byte.class);
    }

    default Map<Double,Short> optMapDoubleShort(String key) {
        return optMap(key, Double.class, Short.class);
    }

    default Map<Double,Short> getMapDoubleShort(String key) {
        return getMap(key, Double.class, Short.class);
    }

    default Map<Double,Integer> optMapDoubleInteger(String key) {
        return optMap(key, Double.class, Integer.class);
    }

    default Map<Double,Integer> getMapDoubleInteger(String key) {
        return getMap(key, Double.class, Integer.class);
    }

    default Map<Double,Long> optMapDoubleLong(String key) {
        return optMap(key, Double.class, Long.class);
    }

    default Map<Double,Long> getMapDoubleLong(String key) {
        return getMap(key, Double.class, Long.class);
    }

    default Map<Double,Float> optMapDoubleFloat(String key) {
        return optMap(key, Double.class, Float.class);
    }

    default Map<Double,Float> getMapDoubleFloat(String key) {
        return getMap(key, Double.class, Float.class);
    }

    default Map<Double,Double> optMapDoubleDouble(String key) {
        return optMap(key, Double.class, Double.class);
    }

    default Map<Double,Double> getMapDoubleDouble(String key) {
        return getMap(key, Double.class, Double.class);
    }

    default Map<Double,OptGet> optMapDoubleOptGet(String key) {
        return optMap(key, Double.class, OptGet.class);
    }

    default Map<Double,OptGet> getMapDoubleOptGet(String key) {
        return getMap(key, Double.class, OptGet.class);
    }

    default <ENUM extends Enum> Map<Double,ENUM> optMapDoubleEnum(String key, Class<ENUM> enumClass) {
        return optMap(key, Double.class, enumClass);
    }

    default <ENUM extends Enum> Map<Double,ENUM>  getMapDoubleEnum(String key, Class<ENUM> enumClass) {
        return getMap(key, Double.class, enumClass);
    }

}
