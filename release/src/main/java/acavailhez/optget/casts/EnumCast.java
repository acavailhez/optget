package acavailhez.optget.casts;

public class EnumCast {
    @SuppressWarnings("unchecked")
    public static <ENUM> ENUM castToEnum(Object unknown, Class<ENUM> enumClass) {
        if (enumClass.isAssignableFrom(unknown.getClass())) {
            return (ENUM) unknown;
        }
        // try to find the correct enum, ignore case
        for (ENUM enumValue : enumClass.getEnumConstants()) {
            if (enumValue.toString().equalsIgnoreCase(unknown.toString())) {
                return enumValue;
            }
        }
        return (ENUM) unknown;
    }

}
