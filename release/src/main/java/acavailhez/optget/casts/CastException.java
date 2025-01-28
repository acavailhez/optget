package acavailhez.optget.casts;

public class CastException extends RuntimeException {

    private Object unknown;
    private Class clazz;

    CastException(Object unknown, Class clazz) {
        super("Cannot cast [" + unknown + "] of class " + unknown.getClass().getSimpleName() + " to " + clazz.getSimpleName());
        this.unknown = unknown;
        this.clazz = clazz;
    }

    CastException(Object unknown, Class clazz, Exception e) {
        super("Cannot cast [" + unknown + "] of class " + unknown.getClass().getSimpleName() + " to " + clazz.getSimpleName(), e);
        this.unknown = unknown;
        this.clazz = clazz;
    }
}
