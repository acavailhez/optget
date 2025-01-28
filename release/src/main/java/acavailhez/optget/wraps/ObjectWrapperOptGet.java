package acavailhez.optget.wraps;

import acavailhez.optget.OptGet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

// A moderately unsafe OptGet wrapper that will attempt to read the getXX methods of an object
public class ObjectWrapperOptGet extends OptGet {


    private final Object wrapped;

    public ObjectWrapperOptGet(final @NotNull Object wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    protected @Nullable Object optToOverride(@NotNull Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("key [" + key + "] must be a String");
        }
        // try to find a public getter matching the key
        String getterName = "get" + capitalizeFirstLetter((String) key);
        try {
            Method getter = wrapped.getClass().getDeclaredMethod(getterName);
            if (Modifier.isPublic(getter.getModifiers())) {
                return getter.invoke(wrapped);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            // expected if the method does not exist
        }

        // try to find a public field matching the key
        try {
            Field field = wrapped.getClass().getField((String) key);
            if (Modifier.isPublic(field.getModifiers())) {
                return field.get(wrapped);
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            // expected if the field does not exist
        }

        // could not find anything
        return null;
    }

    @Override
    public @NotNull Set<Object> keySet() {
        return Set.of();
    }

    private static String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
