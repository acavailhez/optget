import acavailhez.optget.OptGet
import acavailhez.optget.OptGetMap
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger

// Since OptGet contains a lot of repetitive code, we generate it automatically
class GenerateOptGetSource {

    // All classes that will have a shortcut in OptGet
    private final static List<Class> SHORTCUT_CLASSES = [];

    // All classes that will have shortcuts in the map utils, as a key
    private final static List<Class> MAP_KEY_CLASSES = [];

    static {
        SHORTCUT_CLASSES.add(String.class)
        SHORTCUT_CLASSES.add(Byte.class)
        SHORTCUT_CLASSES.add(Short.class)
        SHORTCUT_CLASSES.add(Integer.class)
        SHORTCUT_CLASSES.add(Long.class)
        SHORTCUT_CLASSES.add(Float.class)
        SHORTCUT_CLASSES.add(Double.class)
        SHORTCUT_CLASSES.add(OptGet.class)
        SHORTCUT_CLASSES.add(OptGetMap.class)

        MAP_KEY_CLASSES.add(String.class)
        MAP_KEY_CLASSES.add(Integer.class)
        MAP_KEY_CLASSES.add(Long.class)
        MAP_KEY_CLASSES.add(Float.class)
        MAP_KEY_CLASSES.add(Double.class)
    }

    public static void main(String[] args) {

        BasicConfigurator.configure()

        log.info("Generating source code for OptGet")

        File model = new File('./scripts/src/acavailhez/optget/OptGet.java')
        String code = model.text

        String imports = ""
        code = code.replace("// GENERATE:IMPORTS", imports)

        // Simple shortcuts

        String simpleShortcuts = ""
        SHORTCUT_CLASSES.each { Class classToCast ->
            String name = classToCast.getSimpleName()

            simpleShortcuts += """
    default ${name} opt${name}(Object key) {
        return opt(key, ${name}.class);
    }"""
            simpleShortcuts += System.lineSeparator()

            simpleShortcuts += """
    default ${name} opt${name}(Object key, ${name} defaultValue) {
        return opt(key, ${name}.class, defaultValue);
    }"""
            simpleShortcuts += System.lineSeparator()

            simpleShortcuts += """
    default ${name} get${name}(Object key) {
        return get(key, ${name}.class);
    }"""
            simpleShortcuts += System.lineSeparator()
        }

        code = code.replace("// GENERATE:SIMPLE-SHORTCUTS", simpleShortcuts)

        // List shortcuts

        String listShortcuts = ""
        SHORTCUT_CLASSES.each { Class classToCast ->
            String name = classToCast.getSimpleName()
            listShortcuts += """
    default List<${name}> optList${name}(Object key) {
        return optList(key, ${name}.class);
    }"""
            listShortcuts += System.lineSeparator()

            listShortcuts += """
    default List<${name}> getList${name}(Object key) {
        return getList(key, ${name}.class);
    }"""
            listShortcuts += System.lineSeparator()
        }

        code = code.replace("// GENERATE:LIST-SHORTCUTS", listShortcuts)

        // Map shortcuts

        String mapShortcuts = ""
        MAP_KEY_CLASSES.each { Class keyToCast ->
            String key = keyToCast.getSimpleName()
            SHORTCUT_CLASSES.each { Class valueToCast ->
                String value = valueToCast.getSimpleName()
                mapShortcuts += """
    default Map<${key},${value}> optMap${key}${value}(Object key) {
        return optMap(key, ${key}.class, ${value}.class);
    }"""
                mapShortcuts += System.lineSeparator()

                mapShortcuts += """
    default Map<${key},${value}> getMap${key}${value}(Object key) {
        return getMap(key, ${key}.class, ${value}.class);
    }"""

                mapShortcuts += System.lineSeparator()
            }

            // add ENUM

            mapShortcuts += """
    default <ENUM extends Enum> Map<${key},ENUM> optMap${key}Enum(Object key, Class<ENUM> enumClass) {
        return optMap(key, ${key}.class, enumClass);
    }"""
            mapShortcuts += System.lineSeparator()

            mapShortcuts += """
    default <ENUM extends Enum> Map<${key},ENUM>  getMap${key}Enum(Object key, Class<ENUM> enumClass) {
        return getMap(key, ${key}.class, enumClass);
    }"""

            mapShortcuts += System.lineSeparator()
        }

        code = code.replace("// GENERATE:MAP-SHORTCUTS", mapShortcuts)



        File optGetClassFile = new File('./src/acavailhez/optget/OptGet.java')
        if (!optGetClassFile.exists()) {
            optGetClassFile.createNewFile()
        }
        optGetClassFile.text = code

        log.info("Generated source code for OptGet")
    }

    private static Logger log = Logger.getLogger(GenerateOptGetSource)
}
