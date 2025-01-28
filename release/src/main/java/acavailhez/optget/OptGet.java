package acavailhez.optget;

import acavailhez.optget.casts.*;
import acavailhez.optget.wraps.MapOptGet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

// acavailhez.optget.OptGet wraps an object that only answers to a `public Object opt(Object key) throws Exception` method
// and exposes many shortcut functions to cast objects in desirable formats
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
    @SuppressWarnings("unchecked")
    public @NotNull Set<Entry<Object, Object>> entrySet() {
        return keySet()
                .stream()
                .map(it -> new AbstractMap.SimpleEntry<Object, Object>(it, opt(it)))
                .collect(Collectors.toSet());
    }

    // SETTERS ---

    @Override
    public Object put(final @NotNull Object key, final @Nullable Object value) {
        throw new RuntimeException("Can't modify OptGet, use a MutableOptGet");
    }

    @Override
    public Object remove(final @NotNull Object key) {
        throw new RuntimeException("Can't modify OptGet, use a MutableOptGet");
    }

    @Override
    public void putAll(final @NotNull Map<?, ?> m) {
        throw new RuntimeException("Can't modify OptGet, use a MutableOptGet");
    }

    @Override
    public void clear() {
        throw new RuntimeException("Can't modify OptGet, use a MutableOptGet");
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
        try {
            return cast(nonCast, classToCast);
        } catch (Throwable t) {
            throw new RuntimeException("Cannot read key " + key, t);
        }
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

    public @Nullable String optString(final @NotNull Object key, String defaultValue) {
        return opt(key, String.class, defaultValue);
    }

    public @NotNull String getString(final @NotNull Object key) {
        return get(key, String.class);
    }

    public @Nullable Byte optByte(final @NotNull Object key) {
        return opt(key, Byte.class);
    }

    public @Nullable Byte optByte(final @NotNull Object key, Byte defaultValue) {
        return opt(key, Byte.class, defaultValue);
    }

    public @NotNull Byte getByte(final @NotNull Object key) {
        return get(key, Byte.class);
    }

    public @Nullable Short optShort(final @NotNull Object key) {
        return opt(key, Short.class);
    }

    public @Nullable Short optShort(final @NotNull Object key, Short defaultValue) {
        return opt(key, Short.class, defaultValue);
    }

    public @NotNull Short getShort(final @NotNull Object key) {
        return get(key, Short.class);
    }

    public @Nullable Integer optInteger(final @NotNull Object key) {
        return opt(key, Integer.class);
    }

    public @Nullable Integer optInteger(final @NotNull Object key, Integer defaultValue) {
        return opt(key, Integer.class, defaultValue);
    }

    public @NotNull Integer getInteger(final @NotNull Object key) {
        return get(key, Integer.class);
    }

    public @Nullable Long optLong(final @NotNull Object key) {
        return opt(key, Long.class);
    }

    public @Nullable Long optLong(final @NotNull Object key, Long defaultValue) {
        return opt(key, Long.class, defaultValue);
    }

    public @NotNull Long getLong(final @NotNull Object key) {
        return get(key, Long.class);
    }

    public @Nullable Float optFloat(final @NotNull Object key) {
        return opt(key, Float.class);
    }

    public @Nullable Float optFloat(final @NotNull Object key, Float defaultValue) {
        return opt(key, Float.class, defaultValue);
    }

    public @NotNull Float getFloat(final @NotNull Object key) {
        return get(key, Float.class);
    }

    public @Nullable Double optDouble(final @NotNull Object key) {
        return opt(key, Double.class);
    }

    public @Nullable Double optDouble(final @NotNull Object key, Double defaultValue) {
        return opt(key, Double.class, defaultValue);
    }

    public @NotNull Double getDouble(final @NotNull Object key) {
        return get(key, Double.class);
    }

    public @Nullable OptGet optOptGet(final @NotNull Object key) {
        return opt(key, OptGet.class);
    }

    public @Nullable OptGet optOptGet(final @NotNull Object key, OptGet defaultValue) {
        return opt(key, OptGet.class, defaultValue);
    }

    public @NotNull OptGet getOptGet(final @NotNull Object key) {
        return get(key, OptGet.class);
    }

    public @Nullable MapOptGet optMapOptGet(final @NotNull Object key) {
        return opt(key, MapOptGet.class);
    }

    public @Nullable MapOptGet optMapOptGet(final @NotNull Object key, MapOptGet defaultValue) {
        return opt(key, MapOptGet.class, defaultValue);
    }

    public @NotNull MapOptGet getMapOptGet(final @NotNull Object key) {
        return get(key, MapOptGet.class);
    }


    // GENERATED-END:SIMPLE-SHORTCUTS

    public @Nullable <ENUM extends Enum> ENUM optEnum(final @NotNull Object key, final @NotNull Class<ENUM> enumClass) {
        return opt(key, enumClass);
    }

    public <ENUM extends Enum> ENUM optEnum(final @NotNull Object key, Class<ENUM> enumClass, final @NotNull ENUM defaultValue) {
        return opt(key, enumClass, defaultValue);
    }

    public <ENUM extends Enum> ENUM getEnum(final @NotNull Object key, final @NotNull Class<ENUM> enumClass) {
        return get(key, enumClass);
    }

    // List shortcuts

    public List optList(final @NotNull Object key) {
        return opt(key, List.class);
    }

    public List getList(final @NotNull Object key) {
        return get(key, List.class);
    }

    public <T> List<T> optList(final @NotNull Object key, final @NotNull Class<T> classToCast) {
        List list = opt(key, List.class);
        List<T> listCasted = new LinkedList<T>();
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


    public @Nullable List<String> optListString(final @NotNull Object key) {
        return optList(key, String.class);
    }

    public @NotNull List<String> getListString(final @NotNull Object key) {
        return getList(key, String.class);
    }

    public @Nullable List<Byte> optListByte(final @NotNull Object key) {
        return optList(key, Byte.class);
    }

    public @NotNull List<Byte> getListByte(final @NotNull Object key) {
        return getList(key, Byte.class);
    }

    public @Nullable List<Short> optListShort(final @NotNull Object key) {
        return optList(key, Short.class);
    }

    public @NotNull List<Short> getListShort(final @NotNull Object key) {
        return getList(key, Short.class);
    }

    public @Nullable List<Integer> optListInteger(final @NotNull Object key) {
        return optList(key, Integer.class);
    }

    public @NotNull List<Integer> getListInteger(final @NotNull Object key) {
        return getList(key, Integer.class);
    }

    public @Nullable List<Long> optListLong(final @NotNull Object key) {
        return optList(key, Long.class);
    }

    public @NotNull List<Long> getListLong(final @NotNull Object key) {
        return getList(key, Long.class);
    }

    public @Nullable List<Float> optListFloat(final @NotNull Object key) {
        return optList(key, Float.class);
    }

    public @NotNull List<Float> getListFloat(final @NotNull Object key) {
        return getList(key, Float.class);
    }

    public @Nullable List<Double> optListDouble(final @NotNull Object key) {
        return optList(key, Double.class);
    }

    public @NotNull List<Double> getListDouble(final @NotNull Object key) {
        return getList(key, Double.class);
    }

    public @Nullable List<OptGet> optListOptGet(final @NotNull Object key) {
        return optList(key, OptGet.class);
    }

    public @NotNull List<OptGet> getListOptGet(final @NotNull Object key) {
        return getList(key, OptGet.class);
    }

    public @Nullable List<MapOptGet> optListMapOptGet(final @NotNull Object key) {
        return optList(key, MapOptGet.class);
    }

    public @NotNull List<MapOptGet> getListMapOptGet(final @NotNull Object key) {
        return getList(key, MapOptGet.class);
    }


    // GENERATED-END:LIST-SHORTCUTS

    public <ENUM extends Enum> List<ENUM> optListEnum(Object key, Class<ENUM> enumClass) {
        return optList(key, enumClass);
    }

    public <ENUM extends Enum> List<ENUM> getListEnum(Object key, Class<ENUM> enumClass) {
        return getList(key, enumClass);
    }

    // Map shortcuts

    public @Nullable Map optMap(final @NotNull Object key) {
        return opt(key, Map.class);
    }

    public @NotNull Map getMap(final @NotNull Object key) {
        return get(key, Map.class);
    }

    public @Nullable <KEY, VALUE> Map<KEY, VALUE> optMap(final @NotNull Object key, Class<KEY> keyToCast, final @NotNull Class<VALUE> valueToCast) {
        Map map = opt(key, Map.class);
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
        return mapCasted;
    }

    // GENERATED-BEGIN:MAP-SHORTCUTS


    public @Nullable Map<String, String> optMapStringString(final @NotNull Object key) {
        return optMap(key, String.class, String.class);
    }

    public @NotNull Map<String, String> getMapStringString(final @NotNull Object key) {
        return getMap(key, String.class, String.class);
    }

    public @Nullable Map<String, Byte> optMapStringByte(final @NotNull Object key) {
        return optMap(key, String.class, Byte.class);
    }

    public @NotNull Map<String, Byte> getMapStringByte(final @NotNull Object key) {
        return getMap(key, String.class, Byte.class);
    }

    public @Nullable Map<String, Short> optMapStringShort(final @NotNull Object key) {
        return optMap(key, String.class, Short.class);
    }

    public @NotNull Map<String, Short> getMapStringShort(final @NotNull Object key) {
        return getMap(key, String.class, Short.class);
    }

    public @Nullable Map<String, Integer> optMapStringInteger(final @NotNull Object key) {
        return optMap(key, String.class, Integer.class);
    }

    public @NotNull Map<String, Integer> getMapStringInteger(final @NotNull Object key) {
        return getMap(key, String.class, Integer.class);
    }

    public @Nullable Map<String, Long> optMapStringLong(final @NotNull Object key) {
        return optMap(key, String.class, Long.class);
    }

    public @NotNull Map<String, Long> getMapStringLong(final @NotNull Object key) {
        return getMap(key, String.class, Long.class);
    }

    public @Nullable Map<String, Float> optMapStringFloat(final @NotNull Object key) {
        return optMap(key, String.class, Float.class);
    }

    public @NotNull Map<String, Float> getMapStringFloat(final @NotNull Object key) {
        return getMap(key, String.class, Float.class);
    }

    public @Nullable Map<String, Double> optMapStringDouble(final @NotNull Object key) {
        return optMap(key, String.class, Double.class);
    }

    public @NotNull Map<String, Double> getMapStringDouble(final @NotNull Object key) {
        return getMap(key, String.class, Double.class);
    }

    public @Nullable Map<String, OptGet> optMapStringOptGet(final @NotNull Object key) {
        return optMap(key, String.class, OptGet.class);
    }

    public @NotNull Map<String, OptGet> getMapStringOptGet(final @NotNull Object key) {
        return getMap(key, String.class, OptGet.class);
    }

    public @Nullable Map<String, MapOptGet> optMapStringMapOptGet(final @NotNull Object key) {
        return optMap(key, String.class, MapOptGet.class);
    }

    public @NotNull Map<String, MapOptGet> getMapStringMapOptGet(final @NotNull Object key) {
        return getMap(key, String.class, MapOptGet.class);
    }

    public @Nullable Map<Integer, String> optMapIntegerString(final @NotNull Object key) {
        return optMap(key, Integer.class, String.class);
    }

    public @NotNull Map<Integer, String> getMapIntegerString(final @NotNull Object key) {
        return getMap(key, Integer.class, String.class);
    }

    public @Nullable Map<Integer, Byte> optMapIntegerByte(final @NotNull Object key) {
        return optMap(key, Integer.class, Byte.class);
    }

    public @NotNull Map<Integer, Byte> getMapIntegerByte(final @NotNull Object key) {
        return getMap(key, Integer.class, Byte.class);
    }

    public @Nullable Map<Integer, Short> optMapIntegerShort(final @NotNull Object key) {
        return optMap(key, Integer.class, Short.class);
    }

    public @NotNull Map<Integer, Short> getMapIntegerShort(final @NotNull Object key) {
        return getMap(key, Integer.class, Short.class);
    }

    public @Nullable Map<Integer, Integer> optMapIntegerInteger(final @NotNull Object key) {
        return optMap(key, Integer.class, Integer.class);
    }

    public @NotNull Map<Integer, Integer> getMapIntegerInteger(final @NotNull Object key) {
        return getMap(key, Integer.class, Integer.class);
    }

    public @Nullable Map<Integer, Long> optMapIntegerLong(final @NotNull Object key) {
        return optMap(key, Integer.class, Long.class);
    }

    public @NotNull Map<Integer, Long> getMapIntegerLong(final @NotNull Object key) {
        return getMap(key, Integer.class, Long.class);
    }

    public @Nullable Map<Integer, Float> optMapIntegerFloat(final @NotNull Object key) {
        return optMap(key, Integer.class, Float.class);
    }

    public @NotNull Map<Integer, Float> getMapIntegerFloat(final @NotNull Object key) {
        return getMap(key, Integer.class, Float.class);
    }

    public @Nullable Map<Integer, Double> optMapIntegerDouble(final @NotNull Object key) {
        return optMap(key, Integer.class, Double.class);
    }

    public @NotNull Map<Integer, Double> getMapIntegerDouble(final @NotNull Object key) {
        return getMap(key, Integer.class, Double.class);
    }

    public @Nullable Map<Integer, OptGet> optMapIntegerOptGet(final @NotNull Object key) {
        return optMap(key, Integer.class, OptGet.class);
    }

    public @NotNull Map<Integer, OptGet> getMapIntegerOptGet(final @NotNull Object key) {
        return getMap(key, Integer.class, OptGet.class);
    }

    public @Nullable Map<Integer, MapOptGet> optMapIntegerMapOptGet(final @NotNull Object key) {
        return optMap(key, Integer.class, MapOptGet.class);
    }

    public @NotNull Map<Integer, MapOptGet> getMapIntegerMapOptGet(final @NotNull Object key) {
        return getMap(key, Integer.class, MapOptGet.class);
    }

    public @Nullable Map<Long, String> optMapLongString(final @NotNull Object key) {
        return optMap(key, Long.class, String.class);
    }

    public @NotNull Map<Long, String> getMapLongString(final @NotNull Object key) {
        return getMap(key, Long.class, String.class);
    }

    public @Nullable Map<Long, Byte> optMapLongByte(final @NotNull Object key) {
        return optMap(key, Long.class, Byte.class);
    }

    public @NotNull Map<Long, Byte> getMapLongByte(final @NotNull Object key) {
        return getMap(key, Long.class, Byte.class);
    }

    public @Nullable Map<Long, Short> optMapLongShort(final @NotNull Object key) {
        return optMap(key, Long.class, Short.class);
    }

    public @NotNull Map<Long, Short> getMapLongShort(final @NotNull Object key) {
        return getMap(key, Long.class, Short.class);
    }

    public @Nullable Map<Long, Integer> optMapLongInteger(final @NotNull Object key) {
        return optMap(key, Long.class, Integer.class);
    }

    public @NotNull Map<Long, Integer> getMapLongInteger(final @NotNull Object key) {
        return getMap(key, Long.class, Integer.class);
    }

    public @Nullable Map<Long, Long> optMapLongLong(final @NotNull Object key) {
        return optMap(key, Long.class, Long.class);
    }

    public @NotNull Map<Long, Long> getMapLongLong(final @NotNull Object key) {
        return getMap(key, Long.class, Long.class);
    }

    public @Nullable Map<Long, Float> optMapLongFloat(final @NotNull Object key) {
        return optMap(key, Long.class, Float.class);
    }

    public @NotNull Map<Long, Float> getMapLongFloat(final @NotNull Object key) {
        return getMap(key, Long.class, Float.class);
    }

    public @Nullable Map<Long, Double> optMapLongDouble(final @NotNull Object key) {
        return optMap(key, Long.class, Double.class);
    }

    public @NotNull Map<Long, Double> getMapLongDouble(final @NotNull Object key) {
        return getMap(key, Long.class, Double.class);
    }

    public @Nullable Map<Long, OptGet> optMapLongOptGet(final @NotNull Object key) {
        return optMap(key, Long.class, OptGet.class);
    }

    public @NotNull Map<Long, OptGet> getMapLongOptGet(final @NotNull Object key) {
        return getMap(key, Long.class, OptGet.class);
    }

    public @Nullable Map<Long, MapOptGet> optMapLongMapOptGet(final @NotNull Object key) {
        return optMap(key, Long.class, MapOptGet.class);
    }

    public @NotNull Map<Long, MapOptGet> getMapLongMapOptGet(final @NotNull Object key) {
        return getMap(key, Long.class, MapOptGet.class);
    }

    public @Nullable Map<Float, String> optMapFloatString(final @NotNull Object key) {
        return optMap(key, Float.class, String.class);
    }

    public @NotNull Map<Float, String> getMapFloatString(final @NotNull Object key) {
        return getMap(key, Float.class, String.class);
    }

    public @Nullable Map<Float, Byte> optMapFloatByte(final @NotNull Object key) {
        return optMap(key, Float.class, Byte.class);
    }

    public @NotNull Map<Float, Byte> getMapFloatByte(final @NotNull Object key) {
        return getMap(key, Float.class, Byte.class);
    }

    public @Nullable Map<Float, Short> optMapFloatShort(final @NotNull Object key) {
        return optMap(key, Float.class, Short.class);
    }

    public @NotNull Map<Float, Short> getMapFloatShort(final @NotNull Object key) {
        return getMap(key, Float.class, Short.class);
    }

    public @Nullable Map<Float, Integer> optMapFloatInteger(final @NotNull Object key) {
        return optMap(key, Float.class, Integer.class);
    }

    public @NotNull Map<Float, Integer> getMapFloatInteger(final @NotNull Object key) {
        return getMap(key, Float.class, Integer.class);
    }

    public @Nullable Map<Float, Long> optMapFloatLong(final @NotNull Object key) {
        return optMap(key, Float.class, Long.class);
    }

    public @NotNull Map<Float, Long> getMapFloatLong(final @NotNull Object key) {
        return getMap(key, Float.class, Long.class);
    }

    public @Nullable Map<Float, Float> optMapFloatFloat(final @NotNull Object key) {
        return optMap(key, Float.class, Float.class);
    }

    public @NotNull Map<Float, Float> getMapFloatFloat(final @NotNull Object key) {
        return getMap(key, Float.class, Float.class);
    }

    public @Nullable Map<Float, Double> optMapFloatDouble(final @NotNull Object key) {
        return optMap(key, Float.class, Double.class);
    }

    public @NotNull Map<Float, Double> getMapFloatDouble(final @NotNull Object key) {
        return getMap(key, Float.class, Double.class);
    }

    public @Nullable Map<Float, OptGet> optMapFloatOptGet(final @NotNull Object key) {
        return optMap(key, Float.class, OptGet.class);
    }

    public @NotNull Map<Float, OptGet> getMapFloatOptGet(final @NotNull Object key) {
        return getMap(key, Float.class, OptGet.class);
    }

    public @Nullable Map<Float, MapOptGet> optMapFloatMapOptGet(final @NotNull Object key) {
        return optMap(key, Float.class, MapOptGet.class);
    }

    public @NotNull Map<Float, MapOptGet> getMapFloatMapOptGet(final @NotNull Object key) {
        return getMap(key, Float.class, MapOptGet.class);
    }

    public @Nullable Map<Double, String> optMapDoubleString(final @NotNull Object key) {
        return optMap(key, Double.class, String.class);
    }

    public @NotNull Map<Double, String> getMapDoubleString(final @NotNull Object key) {
        return getMap(key, Double.class, String.class);
    }

    public @Nullable Map<Double, Byte> optMapDoubleByte(final @NotNull Object key) {
        return optMap(key, Double.class, Byte.class);
    }

    public @NotNull Map<Double, Byte> getMapDoubleByte(final @NotNull Object key) {
        return getMap(key, Double.class, Byte.class);
    }

    public @Nullable Map<Double, Short> optMapDoubleShort(final @NotNull Object key) {
        return optMap(key, Double.class, Short.class);
    }

    public @NotNull Map<Double, Short> getMapDoubleShort(final @NotNull Object key) {
        return getMap(key, Double.class, Short.class);
    }

    public @Nullable Map<Double, Integer> optMapDoubleInteger(final @NotNull Object key) {
        return optMap(key, Double.class, Integer.class);
    }

    public @NotNull Map<Double, Integer> getMapDoubleInteger(final @NotNull Object key) {
        return getMap(key, Double.class, Integer.class);
    }

    public @Nullable Map<Double, Long> optMapDoubleLong(final @NotNull Object key) {
        return optMap(key, Double.class, Long.class);
    }

    public @NotNull Map<Double, Long> getMapDoubleLong(final @NotNull Object key) {
        return getMap(key, Double.class, Long.class);
    }

    public @Nullable Map<Double, Float> optMapDoubleFloat(final @NotNull Object key) {
        return optMap(key, Double.class, Float.class);
    }

    public @NotNull Map<Double, Float> getMapDoubleFloat(final @NotNull Object key) {
        return getMap(key, Double.class, Float.class);
    }

    public @Nullable Map<Double, Double> optMapDoubleDouble(final @NotNull Object key) {
        return optMap(key, Double.class, Double.class);
    }

    public @NotNull Map<Double, Double> getMapDoubleDouble(final @NotNull Object key) {
        return getMap(key, Double.class, Double.class);
    }

    public @Nullable Map<Double, OptGet> optMapDoubleOptGet(final @NotNull Object key) {
        return optMap(key, Double.class, OptGet.class);
    }

    public @NotNull Map<Double, OptGet> getMapDoubleOptGet(final @NotNull Object key) {
        return getMap(key, Double.class, OptGet.class);
    }

    public @Nullable Map<Double, MapOptGet> optMapDoubleMapOptGet(final @NotNull Object key) {
        return optMap(key, Double.class, MapOptGet.class);
    }

    public @NotNull Map<Double, MapOptGet> getMapDoubleMapOptGet(final @NotNull Object key) {
        return getMap(key, Double.class, MapOptGet.class);
    }


    // GENERATED-END:MAP-SHORTCUTS
}