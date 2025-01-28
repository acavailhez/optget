public class CodeClass {

    public String className;
    public String alias;

    public CodeClass(String className) {
        this.className = className;
        this.alias = className;
    }

    public CodeClass(String className, String alias) {
        this.className = className;
        this.alias = alias;
    }
}
