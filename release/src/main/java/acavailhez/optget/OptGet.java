package acavailhez.optget;

import acavailhez.optget.casts.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

// OptGet wraps an object that can answer to a "get(Object key)"
// and exposes many shortcut functions to cast objects in desirable formats
// For example optGet.get("features.1.geometry") will search in a Map, a List, and another Map
public abstract class OptGet implements Map<Object, Object> {

    // code used to cast Object to the desired class
    @SuppressWarnings("rawtypes")
    private final Map<Class, AbstractCast> castors = new HashMap<>();

    // How strict we are when casting
    private CastMode castMode = CastMode.CLEAN;

    // init
    protected OptGet() {
        addCast(new StringCast());
        addCast(new LongCast());
        addCast(new IntegerCast());
        addCast(new ShortCast());
        addCast(new ByteCast());
        addCast(new FloatCast());
        addCast(new DoubleCast());
        addCast(new OptGetCast());
    }

    public static OptGet wrap(Object object) {
        return new OptGetCast().cast(object);
    }

    // #####################
    //  Functions to override
    // #####################

    // The main function to override
    // Lookup an object for a simple key (ie "location" instead of "location.latitude")
    // Can return null
    protected abstract @Nullable Object optToOverride(final @NotNull Object key);

    // What to do when a key is missing
    // Typically, throw an IllegalArgumentException
    // but your application might need something else
    protected <T> void onMissingKey(final @NotNull Object key, final @NotNull Class<T> classToCast) {
        throw new IllegalArgumentException("Missing key:" + key + " of class:" + classToCast.getName());
    }

    // What to do when a key exists, but the value is missing
    // Typically, throw an IllegalArgumentException
    // but your application might need something else
    protected <T> void onNullValue(final @NotNull Object key, final @NotNull Class<T> classToCast) {
        throw new IllegalArgumentException("Key:" + key + " of class:" + classToCast.getName() + " has null value");
    }

    // #####################
    //  Map implementation, can be overriden more
    // #####################

    // GETTERS ---

    // keySet() has to be implemented

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return keySet().isEmpty();
    }

    @Override
    public boolean containsKey(final @NotNull Object key) {
        return keySet().contains(key);
    }

    @Override
    public boolean containsValue(final @NotNull Object value) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public @NotNull Collection<Object> values() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public @NotNull Set<Entry<Object, Object>> entrySet() {
        return keySet()
                .stream()
                .map(it -> new AbstractMap.SimpleEntry<Object, Object>(it, opt(it)))
                .collect(Collectors.toSet());
    }

    // SETTERS ---

    // Must implememt put and remove

    @Override
    public void putAll(final @NotNull Map<?, ?> m) {
        for (Map.Entry<?, ?> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (Object key : keySet()) {
            remove(key);
        }
    }

    // #####################
    //  Basics
    // #####################

    // Simplest opt
    public @Nullable Object opt(final @NotNull Object key) {
        return privateOpt(key, Object.class, null);
    }

    // opt with a cast
    public <T> @Nullable T opt(final @NotNull Object key, final @NotNull Class<T> classToCast) {
        return privateOpt(key, classToCast, null);
    }

    // opt with a default value
    public @Nullable Object opt(final @NotNull Object key, final @NotNull Object defaultValue) {
        return privateOpt(key, Object.class, defaultValue);
    }

    // opt with a cast and a default value
    public <T> @NotNull T opt(final @NotNull Object key, final @NotNull Class<T> classToCast, @NotNull T defaultValue) {
        return Objects.requireNonNull(privateOpt(key, classToCast, defaultValue));
    }

    // simplest get
    public @NotNull Object get(final @NotNull Object key) {
        return get(key, Object.class);
    }

    // get with a cast
    public <T> @NotNull T get(final @NotNull Object key, final @NotNull Class<T> classToCast) {
        T value = opt(key, classToCast);
        if (value == null) {
            onNullValue(key.toString(), classToCast);
        }
        return Objects.requireNonNull(value);
    }

    public void setCastMode(final @NotNull CastMode castMode) {
        this.castMode = castMode;
    }

    public <T> void addCast(final @NotNull AbstractCast<T> cast) {
        castors.put(cast.getCastClass(), cast);
    }

    // #####################
    //  Internals
    // #####################

    private <T> @Nullable T privateOpt(final @NotNull Object key, final @NotNull Class<T> classToCast, @Nullable T defaultValue) {
        Object nonCast = recursiveOpt(key);
        if (nonCast == null) {
            return defaultValue;
        }
        return cast(nonCast, classToCast);
    }

    // Will transform getString("key.sub") to getGetOpt("key").getString("sub")
    // when used in groovy, map.key.sub will then work
    private @Nullable Object recursiveOpt(final @NotNull Object key) {
        // First attempt to get the value directly
        Object value = optToOverride(key);
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

    @SuppressWarnings("unchecked")
    private <T> @NotNull T cast(final @NotNull Object unknown, final @NotNull Class<T> classToCast) {
        if (classToCast == Object.class) {
            return (T) unknown;
        }
        if (classToCast.isAssignableFrom(unknown.getClass())) {
            return (T) unknown;
        }
        if (classToCast.isEnum()) {
            return (T) EnumCast.castToEnum(unknown, classToCast);
        }
        if (castors.containsKey(classToCast)) {
            return (T) castors.get(classToCast).cast(unknown, this.castMode);
        }
        throw new IllegalArgumentException("No code to cast to class " + classToCast.getName());
    }


    // #####################
    //  Shortcuts
    // #####################

    // Simple shortcuts

    // GENERATED-BEGIN:SIMPLE-SHORTCUTS


    public @Nullable String optString(final @NotNull Object key) {
        return opt(key, String.class);
    }

    public @Nullable String optString(final @NotNull Object key, final @NotNull String defaultValue) {
        return opt(key, String.class, defaultValue);
    }

    public @NotNull String getString(final @NotNull Object key) {
        return get(key, String.class);
    }

    public @Nullable Byte optByte(final @NotNull Object key) {
        return opt(key, Byte.class);
    }

    public @Nullable Byte optByte(final @NotNull Object key, final @NotNull Byte defaultValue) {
        return opt(key, Byte.class, defaultValue);
    }

    public byte getByte(final @NotNull Object key) {
        return get(key, Byte.class);
    }

    public @Nullable Short optShort(final @NotNull Object key) {
        return opt(key, Short.class);
    }

    public @Nullable Short optShort(final @NotNull Object key, final @NotNull Short defaultValue) {
        return opt(key, Short.class, defaultValue);
    }

    public short getShort(final @NotNull Object key) {
        return get(key, Short.class);
    }

    public @Nullable Integer optInteger(final @NotNull Object key) {
        return opt(key, Integer.class);
    }

    public @Nullable Integer optInteger(final @NotNull Object key, final @NotNull Integer defaultValue) {
        return opt(key, Integer.class, defaultValue);
    }

    public int getInteger(final @NotNull Object key) {
        return get(key, Integer.class);
    }

    public @Nullable Integer optInt(final @NotNull Object key) {
        return opt(key, Integer.class);
    }

    public @Nullable Integer optInt(final @NotNull Object key, final @NotNull Integer defaultValue) {
        return opt(key, Integer.class, defaultValue);
    }

    public int getInt(final @NotNull Object key) {
        return get(key, Integer.class);
    }

    public @Nullable Long optLong(final @NotNull Object key) {
        return opt(key, Long.class);
    }

    public @Nullable Long optLong(final @NotNull Object key, final @NotNull Long defaultValue) {
        return opt(key, Long.class, defaultValue);
    }

    public long getLong(final @NotNull Object key) {
        return get(key, Long.class);
    }

    public @Nullable Float optFloat(final @NotNull Object key) {
        return opt(key, Float.class);
    }

    public @Nullable Float optFloat(final @NotNull Object key, final @NotNull Float defaultValue) {
        return opt(key, Float.class, defaultValue);
    }

    public float getFloat(final @NotNull Object key) {
        return get(key, Float.class);
    }

    public @Nullable Double optDouble(final @NotNull Object key) {
        return opt(key, Double.class);
    }

    public @Nullable Double optDouble(final @NotNull Object key, final @NotNull Double defaultValue) {
        return opt(key, Double.class, defaultValue);
    }

    public double getDouble(final @NotNull Object key) {
        return get(key, Double.class);
    }

    public @Nullable OptGet optOptGet(final @NotNull Object key) {
        return opt(key, OptGet.class);
    }

    public @Nullable OptGet optOptGet(final @NotNull Object key, final @NotNull OptGet defaultValue) {
        return opt(key, OptGet.class, defaultValue);
    }

    public @NotNull OptGet getOptGet(final @NotNull Object key) {
        return get(key, OptGet.class);
    }

    public @Nullable Boolean optBoolean(final @NotNull Object key) {
        return opt(key, Boolean.class);
    }

    public @Nullable Boolean optBoolean(final @NotNull Object key, final @NotNull Boolean defaultValue) {
        return opt(key, Boolean.class, defaultValue);
    }

    public boolean getBoolean(final @NotNull Object key) {
        return get(key, Boolean.class);
    }

    public @Nullable Boolean optBool(final @NotNull Object key) {
        return opt(key, Boolean.class);
    }

    public @Nullable Boolean optBool(final @NotNull Object key, final @NotNull Boolean defaultValue) {
        return opt(key, Boolean.class, defaultValue);
    }

    public boolean getBool(final @NotNull Object key) {
        return get(key, Boolean.class);
    }


    // GENERATED-END:SIMPLE-SHORTCUTS

    @SuppressWarnings("rawtypes")
    public @Nullable <ENUM extends Enum> ENUM optEnum(final @NotNull Object key, final @NotNull Class<ENUM> enumClass) {
        return opt(key, enumClass);
    }

    @SuppressWarnings("rawtypes")
    public <ENUM extends Enum> ENUM optEnum(final @NotNull Object key, Class<ENUM> enumClass, final @NotNull ENUM defaultValue) {
        return opt(key, enumClass, defaultValue);
    }

    @SuppressWarnings("rawtypes")
    public <ENUM extends Enum> ENUM getEnum(final @NotNull Object key, final @NotNull Class<ENUM> enumClass) {
        return get(key, enumClass);
    }

    // List shortcuts

    @SuppressWarnings("rawtypes")
    public List optList(final @NotNull Object key) {
        return opt(key, List.class);
    }

    @SuppressWarnings("rawtypes")
    public List getList(final @NotNull Object key) {
        return get(key, List.class);
    }

    @SuppressWarnings("rawtypes")
    public <T> List<T> optList(final @NotNull Object key, final @NotNull Class<T> classToCast) {
        List list = opt(key, List.class);
        List<T> listCasted = new LinkedList<T>();
        if (list == null) {
            return null;
        }
        for (Object o : list) {
            listCasted.add(cast(o, classToCast));
        }
        return listCasted;
    }

    public <T> List<T> getList(final @NotNull Object key, final @NotNull Class<T> classToCast) {
        List<T> value = optList(key, classToCast);
        if (value == null) {
            onNullValue(key.toString(), List.class);
        }
        return value;
    }

    // GENERATED-BEGIN:LIST-SHORTCUTS


    public @Nullable List<String> optListOfString(final @NotNull Object key) {
        return optList(key, String.class);
    }

    public @NotNull List<String> getListOfString(final @NotNull Object key) {
        return getList(key, String.class);
    }

    public @Nullable List<Byte> optListOfByte(final @NotNull Object key) {
        return optList(key, Byte.class);
    }

    public @NotNull List<Byte> getListOfByte(final @NotNull Object key) {
        return getList(key, Byte.class);
    }

    public @Nullable List<Short> optListOfShort(final @NotNull Object key) {
        return optList(key, Short.class);
    }

    public @NotNull List<Short> getListOfShort(final @NotNull Object key) {
        return getList(key, Short.class);
    }

    public @Nullable List<Integer> optListOfInteger(final @NotNull Object key) {
        return optList(key, Integer.class);
    }

    public @NotNull List<Integer> getListOfInteger(final @NotNull Object key) {
        return getList(key, Integer.class);
    }

    public @Nullable List<Integer> optListOfInt(final @NotNull Object key) {
        return optList(key, Integer.class);
    }

    public @NotNull List<Integer> getListOfInt(final @NotNull Object key) {
        return getList(key, Integer.class);
    }

    public @Nullable List<Long> optListOfLong(final @NotNull Object key) {
        return optList(key, Long.class);
    }

    public @NotNull List<Long> getListOfLong(final @NotNull Object key) {
        return getList(key, Long.class);
    }

    public @Nullable List<Float> optListOfFloat(final @NotNull Object key) {
        return optList(key, Float.class);
    }

    public @NotNull List<Float> getListOfFloat(final @NotNull Object key) {
        return getList(key, Float.class);
    }

    public @Nullable List<Double> optListOfDouble(final @NotNull Object key) {
        return optList(key, Double.class);
    }

    public @NotNull List<Double> getListOfDouble(final @NotNull Object key) {
        return getList(key, Double.class);
    }

    public @Nullable List<OptGet> optListOfOptGet(final @NotNull Object key) {
        return optList(key, OptGet.class);
    }

    public @NotNull List<OptGet> getListOfOptGet(final @NotNull Object key) {
        return getList(key, OptGet.class);
    }

    public @Nullable List<Boolean> optListOfBoolean(final @NotNull Object key) {
        return optList(key, Boolean.class);
    }

    public @NotNull List<Boolean> getListOfBoolean(final @NotNull Object key) {
        return getList(key, Boolean.class);
    }

    public @Nullable List<Boolean> optListOfBool(final @NotNull Object key) {
        return optList(key, Boolean.class);
    }

    public @NotNull List<Boolean> getListOfBool(final @NotNull Object key) {
        return getList(key, Boolean.class);
    }


    // GENERATED-END:LIST-SHORTCUTS

    @SuppressWarnings("rawtypes")
    public <ENUM extends Enum> List<ENUM> optListEnum(Object key, Class<ENUM> enumClass) {
        return optList(key, enumClass);
    }

    @SuppressWarnings("rawtypes")
    public <ENUM extends Enum> List<ENUM> getListEnum(Object key, Class<ENUM> enumClass) {
        return getList(key, enumClass);
    }

    // Map shortcuts

    @SuppressWarnings("rawtypes")
    public @Nullable Map optMap(final @NotNull Object key) {
        return opt(key, Map.class);
    }

    @SuppressWarnings("rawtypes")
    public @NotNull Map getMap(final @NotNull Object key) {
        return get(key, Map.class);
    }

    @SuppressWarnings("rawtypes")
    public @Nullable <KEY, VALUE> Map<KEY, VALUE> optMap(final @NotNull Object key, Class<KEY> keyToCast, final @NotNull Class<VALUE> valueToCast) {
        Map map = opt(key, Map.class);
        if (map == null) {
            return null;
        }
        Map<KEY, VALUE> mapCasted = new HashMap<>();
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            mapCasted.put(cast(entry.getKey(), keyToCast), cast(entry.getValue(), valueToCast));
        }
        return mapCasted;
    }

    public @NotNull <KEY, VALUE> Map<KEY, VALUE> getMap(final @NotNull Object key, final @NotNull Class<KEY> keyToCast, Class<VALUE> valueToCast) {
        Map<KEY, VALUE> mapCasted = optMap(key, keyToCast, valueToCast);
        if (mapCasted == null) {
            onNullValue(key, Map.class);
        }
        return Objects.requireNonNull(mapCasted);
    }

    // GENERATED-BEGIN:MAP-SHORTCUTS


    public @Nullable Map<String, String> optMapOfStringToString(final @NotNull Object key) {
        return optMap(key, String.class, String.class);
    }

    public @NotNull Map<String, String> getMapOfStringToString(final @NotNull Object key) {
        return getMap(key, String.class, String.class);
    }

    public @Nullable Map<String, Byte> optMapOfStringToByte(final @NotNull Object key) {
        return optMap(key, String.class, Byte.class);
    }

    public @NotNull Map<String, Byte> getMapOfStringToByte(final @NotNull Object key) {
        return getMap(key, String.class, Byte.class);
    }

    public @Nullable Map<String, Short> optMapOfStringToShort(final @NotNull Object key) {
        return optMap(key, String.class, Short.class);
    }

    public @NotNull Map<String, Short> getMapOfStringToShort(final @NotNull Object key) {
        return getMap(key, String.class, Short.class);
    }

    public @Nullable Map<String, Integer> optMapOfStringToInteger(final @NotNull Object key) {
        return optMap(key, String.class, Integer.class);
    }

    public @NotNull Map<String, Integer> getMapOfStringToInteger(final @NotNull Object key) {
        return getMap(key, String.class, Integer.class);
    }

    public @Nullable Map<String, Integer> optMapOfStringToInt(final @NotNull Object key) {
        return optMap(key, String.class, Integer.class);
    }

    public @NotNull Map<String, Integer> getMapOfStringToInt(final @NotNull Object key) {
        return getMap(key, String.class, Integer.class);
    }

    public @Nullable Map<String, Long> optMapOfStringToLong(final @NotNull Object key) {
        return optMap(key, String.class, Long.class);
    }

    public @NotNull Map<String, Long> getMapOfStringToLong(final @NotNull Object key) {
        return getMap(key, String.class, Long.class);
    }

    public @Nullable Map<String, Float> optMapOfStringToFloat(final @NotNull Object key) {
        return optMap(key, String.class, Float.class);
    }

    public @NotNull Map<String, Float> getMapOfStringToFloat(final @NotNull Object key) {
        return getMap(key, String.class, Float.class);
    }

    public @Nullable Map<String, Double> optMapOfStringToDouble(final @NotNull Object key) {
        return optMap(key, String.class, Double.class);
    }

    public @NotNull Map<String, Double> getMapOfStringToDouble(final @NotNull Object key) {
        return getMap(key, String.class, Double.class);
    }

    public @Nullable Map<String, OptGet> optMapOfStringToOptGet(final @NotNull Object key) {
        return optMap(key, String.class, OptGet.class);
    }

    public @NotNull Map<String, OptGet> getMapOfStringToOptGet(final @NotNull Object key) {
        return getMap(key, String.class, OptGet.class);
    }

    public @Nullable Map<String, Boolean> optMapOfStringToBoolean(final @NotNull Object key) {
        return optMap(key, String.class, Boolean.class);
    }

    public @NotNull Map<String, Boolean> getMapOfStringToBoolean(final @NotNull Object key) {
        return getMap(key, String.class, Boolean.class);
    }

    public @Nullable Map<String, Boolean> optMapOfStringToBool(final @NotNull Object key) {
        return optMap(key, String.class, Boolean.class);
    }

    public @NotNull Map<String, Boolean> getMapOfStringToBool(final @NotNull Object key) {
        return getMap(key, String.class, Boolean.class);
    }

    public @Nullable Map<Integer, String> optMapOfIntegerToString(final @NotNull Object key) {
        return optMap(key, Integer.class, String.class);
    }

    public @NotNull Map<Integer, String> getMapOfIntegerToString(final @NotNull Object key) {
        return getMap(key, Integer.class, String.class);
    }

    public @Nullable Map<Integer, Byte> optMapOfIntegerToByte(final @NotNull Object key) {
        return optMap(key, Integer.class, Byte.class);
    }

    public @NotNull Map<Integer, Byte> getMapOfIntegerToByte(final @NotNull Object key) {
        return getMap(key, Integer.class, Byte.class);
    }

    public @Nullable Map<Integer, Short> optMapOfIntegerToShort(final @NotNull Object key) {
        return optMap(key, Integer.class, Short.class);
    }

    public @NotNull Map<Integer, Short> getMapOfIntegerToShort(final @NotNull Object key) {
        return getMap(key, Integer.class, Short.class);
    }

    public @Nullable Map<Integer, Integer> optMapOfIntegerToInteger(final @NotNull Object key) {
        return optMap(key, Integer.class, Integer.class);
    }

    public @NotNull Map<Integer, Integer> getMapOfIntegerToInteger(final @NotNull Object key) {
        return getMap(key, Integer.class, Integer.class);
    }

    public @Nullable Map<Integer, Integer> optMapOfIntegerToInt(final @NotNull Object key) {
        return optMap(key, Integer.class, Integer.class);
    }

    public @NotNull Map<Integer, Integer> getMapOfIntegerToInt(final @NotNull Object key) {
        return getMap(key, Integer.class, Integer.class);
    }

    public @Nullable Map<Integer, Long> optMapOfIntegerToLong(final @NotNull Object key) {
        return optMap(key, Integer.class, Long.class);
    }

    public @NotNull Map<Integer, Long> getMapOfIntegerToLong(final @NotNull Object key) {
        return getMap(key, Integer.class, Long.class);
    }

    public @Nullable Map<Integer, Float> optMapOfIntegerToFloat(final @NotNull Object key) {
        return optMap(key, Integer.class, Float.class);
    }

    public @NotNull Map<Integer, Float> getMapOfIntegerToFloat(final @NotNull Object key) {
        return getMap(key, Integer.class, Float.class);
    }

    public @Nullable Map<Integer, Double> optMapOfIntegerToDouble(final @NotNull Object key) {
        return optMap(key, Integer.class, Double.class);
    }

    public @NotNull Map<Integer, Double> getMapOfIntegerToDouble(final @NotNull Object key) {
        return getMap(key, Integer.class, Double.class);
    }

    public @Nullable Map<Integer, OptGet> optMapOfIntegerToOptGet(final @NotNull Object key) {
        return optMap(key, Integer.class, OptGet.class);
    }

    public @NotNull Map<Integer, OptGet> getMapOfIntegerToOptGet(final @NotNull Object key) {
        return getMap(key, Integer.class, OptGet.class);
    }

    public @Nullable Map<Integer, Boolean> optMapOfIntegerToBoolean(final @NotNull Object key) {
        return optMap(key, Integer.class, Boolean.class);
    }

    public @NotNull Map<Integer, Boolean> getMapOfIntegerToBoolean(final @NotNull Object key) {
        return getMap(key, Integer.class, Boolean.class);
    }

    public @Nullable Map<Integer, Boolean> optMapOfIntegerToBool(final @NotNull Object key) {
        return optMap(key, Integer.class, Boolean.class);
    }

    public @NotNull Map<Integer, Boolean> getMapOfIntegerToBool(final @NotNull Object key) {
        return getMap(key, Integer.class, Boolean.class);
    }

    public @Nullable Map<Integer, String> optMapOfIntToString(final @NotNull Object key) {
        return optMap(key, Integer.class, String.class);
    }

    public @NotNull Map<Integer, String> getMapOfIntToString(final @NotNull Object key) {
        return getMap(key, Integer.class, String.class);
    }

    public @Nullable Map<Integer, Byte> optMapOfIntToByte(final @NotNull Object key) {
        return optMap(key, Integer.class, Byte.class);
    }

    public @NotNull Map<Integer, Byte> getMapOfIntToByte(final @NotNull Object key) {
        return getMap(key, Integer.class, Byte.class);
    }

    public @Nullable Map<Integer, Short> optMapOfIntToShort(final @NotNull Object key) {
        return optMap(key, Integer.class, Short.class);
    }

    public @NotNull Map<Integer, Short> getMapOfIntToShort(final @NotNull Object key) {
        return getMap(key, Integer.class, Short.class);
    }

    public @Nullable Map<Integer, Integer> optMapOfIntToInteger(final @NotNull Object key) {
        return optMap(key, Integer.class, Integer.class);
    }

    public @NotNull Map<Integer, Integer> getMapOfIntToInteger(final @NotNull Object key) {
        return getMap(key, Integer.class, Integer.class);
    }

    public @Nullable Map<Integer, Integer> optMapOfIntToInt(final @NotNull Object key) {
        return optMap(key, Integer.class, Integer.class);
    }

    public @NotNull Map<Integer, Integer> getMapOfIntToInt(final @NotNull Object key) {
        return getMap(key, Integer.class, Integer.class);
    }

    public @Nullable Map<Integer, Long> optMapOfIntToLong(final @NotNull Object key) {
        return optMap(key, Integer.class, Long.class);
    }

    public @NotNull Map<Integer, Long> getMapOfIntToLong(final @NotNull Object key) {
        return getMap(key, Integer.class, Long.class);
    }

    public @Nullable Map<Integer, Float> optMapOfIntToFloat(final @NotNull Object key) {
        return optMap(key, Integer.class, Float.class);
    }

    public @NotNull Map<Integer, Float> getMapOfIntToFloat(final @NotNull Object key) {
        return getMap(key, Integer.class, Float.class);
    }

    public @Nullable Map<Integer, Double> optMapOfIntToDouble(final @NotNull Object key) {
        return optMap(key, Integer.class, Double.class);
    }

    public @NotNull Map<Integer, Double> getMapOfIntToDouble(final @NotNull Object key) {
        return getMap(key, Integer.class, Double.class);
    }

    public @Nullable Map<Integer, OptGet> optMapOfIntToOptGet(final @NotNull Object key) {
        return optMap(key, Integer.class, OptGet.class);
    }

    public @NotNull Map<Integer, OptGet> getMapOfIntToOptGet(final @NotNull Object key) {
        return getMap(key, Integer.class, OptGet.class);
    }

    public @Nullable Map<Integer, Boolean> optMapOfIntToBoolean(final @NotNull Object key) {
        return optMap(key, Integer.class, Boolean.class);
    }

    public @NotNull Map<Integer, Boolean> getMapOfIntToBoolean(final @NotNull Object key) {
        return getMap(key, Integer.class, Boolean.class);
    }

    public @Nullable Map<Integer, Boolean> optMapOfIntToBool(final @NotNull Object key) {
        return optMap(key, Integer.class, Boolean.class);
    }

    public @NotNull Map<Integer, Boolean> getMapOfIntToBool(final @NotNull Object key) {
        return getMap(key, Integer.class, Boolean.class);
    }

    public @Nullable Map<Long, String> optMapOfLongToString(final @NotNull Object key) {
        return optMap(key, Long.class, String.class);
    }

    public @NotNull Map<Long, String> getMapOfLongToString(final @NotNull Object key) {
        return getMap(key, Long.class, String.class);
    }

    public @Nullable Map<Long, Byte> optMapOfLongToByte(final @NotNull Object key) {
        return optMap(key, Long.class, Byte.class);
    }

    public @NotNull Map<Long, Byte> getMapOfLongToByte(final @NotNull Object key) {
        return getMap(key, Long.class, Byte.class);
    }

    public @Nullable Map<Long, Short> optMapOfLongToShort(final @NotNull Object key) {
        return optMap(key, Long.class, Short.class);
    }

    public @NotNull Map<Long, Short> getMapOfLongToShort(final @NotNull Object key) {
        return getMap(key, Long.class, Short.class);
    }

    public @Nullable Map<Long, Integer> optMapOfLongToInteger(final @NotNull Object key) {
        return optMap(key, Long.class, Integer.class);
    }

    public @NotNull Map<Long, Integer> getMapOfLongToInteger(final @NotNull Object key) {
        return getMap(key, Long.class, Integer.class);
    }

    public @Nullable Map<Long, Integer> optMapOfLongToInt(final @NotNull Object key) {
        return optMap(key, Long.class, Integer.class);
    }

    public @NotNull Map<Long, Integer> getMapOfLongToInt(final @NotNull Object key) {
        return getMap(key, Long.class, Integer.class);
    }

    public @Nullable Map<Long, Long> optMapOfLongToLong(final @NotNull Object key) {
        return optMap(key, Long.class, Long.class);
    }

    public @NotNull Map<Long, Long> getMapOfLongToLong(final @NotNull Object key) {
        return getMap(key, Long.class, Long.class);
    }

    public @Nullable Map<Long, Float> optMapOfLongToFloat(final @NotNull Object key) {
        return optMap(key, Long.class, Float.class);
    }

    public @NotNull Map<Long, Float> getMapOfLongToFloat(final @NotNull Object key) {
        return getMap(key, Long.class, Float.class);
    }

    public @Nullable Map<Long, Double> optMapOfLongToDouble(final @NotNull Object key) {
        return optMap(key, Long.class, Double.class);
    }

    public @NotNull Map<Long, Double> getMapOfLongToDouble(final @NotNull Object key) {
        return getMap(key, Long.class, Double.class);
    }

    public @Nullable Map<Long, OptGet> optMapOfLongToOptGet(final @NotNull Object key) {
        return optMap(key, Long.class, OptGet.class);
    }

    public @NotNull Map<Long, OptGet> getMapOfLongToOptGet(final @NotNull Object key) {
        return getMap(key, Long.class, OptGet.class);
    }

    public @Nullable Map<Long, Boolean> optMapOfLongToBoolean(final @NotNull Object key) {
        return optMap(key, Long.class, Boolean.class);
    }

    public @NotNull Map<Long, Boolean> getMapOfLongToBoolean(final @NotNull Object key) {
        return getMap(key, Long.class, Boolean.class);
    }

    public @Nullable Map<Long, Boolean> optMapOfLongToBool(final @NotNull Object key) {
        return optMap(key, Long.class, Boolean.class);
    }

    public @NotNull Map<Long, Boolean> getMapOfLongToBool(final @NotNull Object key) {
        return getMap(key, Long.class, Boolean.class);
    }

    public @Nullable Map<Float, String> optMapOfFloatToString(final @NotNull Object key) {
        return optMap(key, Float.class, String.class);
    }

    public @NotNull Map<Float, String> getMapOfFloatToString(final @NotNull Object key) {
        return getMap(key, Float.class, String.class);
    }

    public @Nullable Map<Float, Byte> optMapOfFloatToByte(final @NotNull Object key) {
        return optMap(key, Float.class, Byte.class);
    }

    public @NotNull Map<Float, Byte> getMapOfFloatToByte(final @NotNull Object key) {
        return getMap(key, Float.class, Byte.class);
    }

    public @Nullable Map<Float, Short> optMapOfFloatToShort(final @NotNull Object key) {
        return optMap(key, Float.class, Short.class);
    }

    public @NotNull Map<Float, Short> getMapOfFloatToShort(final @NotNull Object key) {
        return getMap(key, Float.class, Short.class);
    }

    public @Nullable Map<Float, Integer> optMapOfFloatToInteger(final @NotNull Object key) {
        return optMap(key, Float.class, Integer.class);
    }

    public @NotNull Map<Float, Integer> getMapOfFloatToInteger(final @NotNull Object key) {
        return getMap(key, Float.class, Integer.class);
    }

    public @Nullable Map<Float, Integer> optMapOfFloatToInt(final @NotNull Object key) {
        return optMap(key, Float.class, Integer.class);
    }

    public @NotNull Map<Float, Integer> getMapOfFloatToInt(final @NotNull Object key) {
        return getMap(key, Float.class, Integer.class);
    }

    public @Nullable Map<Float, Long> optMapOfFloatToLong(final @NotNull Object key) {
        return optMap(key, Float.class, Long.class);
    }

    public @NotNull Map<Float, Long> getMapOfFloatToLong(final @NotNull Object key) {
        return getMap(key, Float.class, Long.class);
    }

    public @Nullable Map<Float, Float> optMapOfFloatToFloat(final @NotNull Object key) {
        return optMap(key, Float.class, Float.class);
    }

    public @NotNull Map<Float, Float> getMapOfFloatToFloat(final @NotNull Object key) {
        return getMap(key, Float.class, Float.class);
    }

    public @Nullable Map<Float, Double> optMapOfFloatToDouble(final @NotNull Object key) {
        return optMap(key, Float.class, Double.class);
    }

    public @NotNull Map<Float, Double> getMapOfFloatToDouble(final @NotNull Object key) {
        return getMap(key, Float.class, Double.class);
    }

    public @Nullable Map<Float, OptGet> optMapOfFloatToOptGet(final @NotNull Object key) {
        return optMap(key, Float.class, OptGet.class);
    }

    public @NotNull Map<Float, OptGet> getMapOfFloatToOptGet(final @NotNull Object key) {
        return getMap(key, Float.class, OptGet.class);
    }

    public @Nullable Map<Float, Boolean> optMapOfFloatToBoolean(final @NotNull Object key) {
        return optMap(key, Float.class, Boolean.class);
    }

    public @NotNull Map<Float, Boolean> getMapOfFloatToBoolean(final @NotNull Object key) {
        return getMap(key, Float.class, Boolean.class);
    }

    public @Nullable Map<Float, Boolean> optMapOfFloatToBool(final @NotNull Object key) {
        return optMap(key, Float.class, Boolean.class);
    }

    public @NotNull Map<Float, Boolean> getMapOfFloatToBool(final @NotNull Object key) {
        return getMap(key, Float.class, Boolean.class);
    }

    public @Nullable Map<Double, String> optMapOfDoubleToString(final @NotNull Object key) {
        return optMap(key, Double.class, String.class);
    }

    public @NotNull Map<Double, String> getMapOfDoubleToString(final @NotNull Object key) {
        return getMap(key, Double.class, String.class);
    }

    public @Nullable Map<Double, Byte> optMapOfDoubleToByte(final @NotNull Object key) {
        return optMap(key, Double.class, Byte.class);
    }

    public @NotNull Map<Double, Byte> getMapOfDoubleToByte(final @NotNull Object key) {
        return getMap(key, Double.class, Byte.class);
    }

    public @Nullable Map<Double, Short> optMapOfDoubleToShort(final @NotNull Object key) {
        return optMap(key, Double.class, Short.class);
    }

    public @NotNull Map<Double, Short> getMapOfDoubleToShort(final @NotNull Object key) {
        return getMap(key, Double.class, Short.class);
    }

    public @Nullable Map<Double, Integer> optMapOfDoubleToInteger(final @NotNull Object key) {
        return optMap(key, Double.class, Integer.class);
    }

    public @NotNull Map<Double, Integer> getMapOfDoubleToInteger(final @NotNull Object key) {
        return getMap(key, Double.class, Integer.class);
    }

    public @Nullable Map<Double, Integer> optMapOfDoubleToInt(final @NotNull Object key) {
        return optMap(key, Double.class, Integer.class);
    }

    public @NotNull Map<Double, Integer> getMapOfDoubleToInt(final @NotNull Object key) {
        return getMap(key, Double.class, Integer.class);
    }

    public @Nullable Map<Double, Long> optMapOfDoubleToLong(final @NotNull Object key) {
        return optMap(key, Double.class, Long.class);
    }

    public @NotNull Map<Double, Long> getMapOfDoubleToLong(final @NotNull Object key) {
        return getMap(key, Double.class, Long.class);
    }

    public @Nullable Map<Double, Float> optMapOfDoubleToFloat(final @NotNull Object key) {
        return optMap(key, Double.class, Float.class);
    }

    public @NotNull Map<Double, Float> getMapOfDoubleToFloat(final @NotNull Object key) {
        return getMap(key, Double.class, Float.class);
    }

    public @Nullable Map<Double, Double> optMapOfDoubleToDouble(final @NotNull Object key) {
        return optMap(key, Double.class, Double.class);
    }

    public @NotNull Map<Double, Double> getMapOfDoubleToDouble(final @NotNull Object key) {
        return getMap(key, Double.class, Double.class);
    }

    public @Nullable Map<Double, OptGet> optMapOfDoubleToOptGet(final @NotNull Object key) {
        return optMap(key, Double.class, OptGet.class);
    }

    public @NotNull Map<Double, OptGet> getMapOfDoubleToOptGet(final @NotNull Object key) {
        return getMap(key, Double.class, OptGet.class);
    }

    public @Nullable Map<Double, Boolean> optMapOfDoubleToBoolean(final @NotNull Object key) {
        return optMap(key, Double.class, Boolean.class);
    }

    public @NotNull Map<Double, Boolean> getMapOfDoubleToBoolean(final @NotNull Object key) {
        return getMap(key, Double.class, Boolean.class);
    }

    public @Nullable Map<Double, Boolean> optMapOfDoubleToBool(final @NotNull Object key) {
        return optMap(key, Double.class, Boolean.class);
    }

    public @NotNull Map<Double, Boolean> getMapOfDoubleToBool(final @NotNull Object key) {
        return getMap(key, Double.class, Boolean.class);
    }


    // GENERATED-END:MAP-SHORTCUTS
}