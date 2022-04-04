public class GenerateOptGetSource extends GroovyObjectSupport {
    public static void main(java.lang.String[] args) {

        BasicConfigurator.invokeMethod("configure", new java.lang.Object[0]);

        log.invokeMethod("info", new java.lang.Object[]{"Generating source code for OptGet"});

        java.io.File model = new java.io.File("./scripts/src/acavailhez/optget/OptGet.java");
        java.lang.String code = model.text;

        java.lang.String imports = "";
        code = code.replace("// GENERATE:IMPORTS", imports);

        // Simple shortcuts

        final Reference<java.lang.String> simpleShortcuts = new groovy.lang.Reference<java.lang.String>("");
        SHORTCUT_CLASSES.invokeMethod("each", new java.lang.Object[]{new Closure(this, this) {
            public java.lang.Object doCall(java.lang.Class classToCast) {
                java.lang.String name = classToCast.getSimpleName();

                simpleShortcuts.set(simpleShortcuts.get() + "\n    default " + name + " opt" + name + "(Object key) {\n        return opt(key, " + name + ".class);\n    }");
                simpleShortcuts.set(simpleShortcuts.get() + java.lang.System.lineSeparator());

                simpleShortcuts.set(simpleShortcuts.get() + "\n    default " + name + " opt" + name + "(Object key, " + name + " defaultValue) {\n        return opt(key, " + name + ".class, defaultValue);\n    }");
                simpleShortcuts.set(simpleShortcuts.get() + java.lang.System.lineSeparator());

                simpleShortcuts.set(simpleShortcuts.get() + "\n    default " + name + " get" + name + "(Object key) {\n        return get(key, " + name + ".class);\n    }");
                return setGroovyRef(simpleShortcuts, simpleShortcuts.get() + java.lang.System.lineSeparator());
            }

        }});

        code = code.replace("// GENERATE:SIMPLE-SHORTCUTS", simpleShortcuts.get());

        // List shortcuts

        final Reference<java.lang.String> listShortcuts = new groovy.lang.Reference<java.lang.String>("");
        SHORTCUT_CLASSES.invokeMethod("each", new java.lang.Object[]{new Closure(this, this) {
            public java.lang.Object doCall(java.lang.Class classToCast) {
                java.lang.String name = classToCast.getSimpleName();
                listShortcuts.set(listShortcuts.get() + "\n    default List<" + name + "> optList" + name + "(Object key) {\n        return optList(key, " + name + ".class);\n    }");
                listShortcuts.set(listShortcuts.get() + java.lang.System.lineSeparator());

                listShortcuts.set(listShortcuts.get() + "\n    default List<" + name + "> getList" + name + "(Object key) {\n        return getList(key, " + name + ".class);\n    }");
                return setGroovyRef(listShortcuts, listShortcuts.get() + java.lang.System.lineSeparator());
            }

        }});

        code = code.replace("// GENERATE:LIST-SHORTCUTS", listShortcuts.get());

        // Map shortcuts

        final Reference<java.lang.String> mapShortcuts = new groovy.lang.Reference<java.lang.String>("");
        MAP_KEY_CLASSES.invokeMethod("each", new java.lang.Object[]{new Closure(this, this) {
            public java.lang.Object doCall(java.lang.Class keyToCast) {
                java.lang.String key = keyToCast.getSimpleName();
                SHORTCUT_CLASSES.invokeMethod("each", new java.lang.Object[]{new Closure(DUMMY__1234567890_DUMMYYYYYY___.this, DUMMY__1234567890_DUMMYYYYYY___.this) {
                    public java.lang.Object doCall(java.lang.Class valueToCast) {
                        java.lang.String value = valueToCast.getSimpleName();
                        mapShortcuts.set(mapShortcuts.get() + "\n    default Map<" + key + "," + value + "> optMap" + key + value + "(Object key) {\n        return optMap(key, " + key + ".class, " + value + ".class);\n    }");
                        mapShortcuts.set(mapShortcuts.get() + java.lang.System.lineSeparator());

                        mapShortcuts.set(mapShortcuts.get() + "\n    default Map<" + key + "," + value + "> getMap" + key + value + "(Object key) {\n        return getMap(key, " + key + ".class, " + value + ".class);\n    }");

                        return setGroovyRef(mapShortcuts, mapShortcuts.get() + java.lang.System.lineSeparator());
                    }

                }});

                // add ENUM

                mapShortcuts.set(mapShortcuts.get() + "\n    default <ENUM extends Enum> Map<" + key + ",ENUM> optMap" + key + "Enum(Object key, Class<ENUM> enumClass) {\n        return optMap(key, " + key + ".class, enumClass);\n    }");
                mapShortcuts.set(mapShortcuts.get() + java.lang.System.lineSeparator());

                mapShortcuts.set(mapShortcuts.get() + "\n    default <ENUM extends Enum> Map<" + key + ",ENUM>  getMap" + key + "Enum(Object key, Class<ENUM> enumClass) {\n        return getMap(key, " + key + ".class, enumClass);\n    }");

                return setGroovyRef(mapShortcuts, mapShortcuts.get() + java.lang.System.lineSeparator());
            }

        }});

        code = code.replace("// GENERATE:MAP-SHORTCUTS", mapShortcuts.get());


        java.io.File optGetClassFile = new java.io.File("./src/acavailhez/optget/OptGet.java");
        if (!optGetClassFile.exists().asBoolean()) {
            optGetClassFile.createNewFile();
        }

        optGetClassFile.text = code;

        log.invokeMethod("info", new java.lang.Object[]{"Generated source code for OptGet"});
    }

    private static final java.util.List<java.lang.Class> SHORTCUT_CLASSES = new java.util.ArrayList<java.lang.Class>();
    private static final java.util.List<java.lang.Class> MAP_KEY_CLASSES = new java.util.ArrayList<java.lang.Class>();
    private static Logger log = Logger.invokeMethod("getLogger", new java.lang.Object[]{GenerateOptGetSource});

    private static <T> T setGroovyRef(groovy.lang.Reference<T> ref, T newValue) {
        ref.set(newValue);
        return newValue;
    }
}
