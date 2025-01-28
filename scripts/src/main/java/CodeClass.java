public class CodeClass {

    public final String className;
    public final String primitiveName;
    public final String alias;

    public CodeClass(String className) {
        this.className = className;
        this.primitiveName = null;
        this.alias = className;
    }

    public CodeClass(String className, String nonNullClassName, String alias) {
        this.className = className;
        this.primitiveName = nonNullClassName;
        this.alias = alias;
    }
}
