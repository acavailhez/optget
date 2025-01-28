import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateOptGetCode {
    private static final Logger log = LogManager.getLogger(GenerateOptGetCode.class);

    // All classes that will have a shortcut in acavailhez.optget.OptGet
    private final static List<CodeClass> SHORTCUT_CLASSES = new ArrayList<>();

    // All classes that will have shortcuts in the map utils, as a key
    private final static List<CodeClass> MAP_KEY_CLASSES = new ArrayList<>();

    private final static String BR = System.lineSeparator();
    private final static String TAB = "    ";

    public static void main(String[] args) throws IOException {
        log.info("hello");
        log.info("Generating source code for OptGet");

        SHORTCUT_CLASSES.add(new CodeClass("String"));
        SHORTCUT_CLASSES.add(new CodeClass("Byte", "byte", "Byte"));
        SHORTCUT_CLASSES.add(new CodeClass("Short", "short", "Short"));
        SHORTCUT_CLASSES.add(new CodeClass("Integer", "int", "Integer"));
        SHORTCUT_CLASSES.add(new CodeClass("Integer", "int", "Int"));
        SHORTCUT_CLASSES.add(new CodeClass("Long", "long", "Long"));
        SHORTCUT_CLASSES.add(new CodeClass("Float", "float", "Float"));
        SHORTCUT_CLASSES.add(new CodeClass("Double", "double", "Double"));
        SHORTCUT_CLASSES.add(new CodeClass("OptGet"));
        SHORTCUT_CLASSES.add(new CodeClass("Boolean", "boolean", "Boolean"));
        SHORTCUT_CLASSES.add(new CodeClass("Boolean", "boolean", "Bool"));

        MAP_KEY_CLASSES.add(new CodeClass("String"));
        MAP_KEY_CLASSES.add(new CodeClass("Integer", "int", "Integer"));
        MAP_KEY_CLASSES.add(new CodeClass("Integer", "int", "Int"));
        MAP_KEY_CLASSES.add(new CodeClass("Long"));
        MAP_KEY_CLASSES.add(new CodeClass("Float"));
        MAP_KEY_CLASSES.add(new CodeClass("Double"));

        File model = new File("./release/src/main/java/acavailhez/optget/OptGet.java");
        String code = FileUtils.readFileToString(model, "UTF-8");

        // Simple shortcuts
        String simpleShortcuts = "";
        for (CodeClass classToCast : SHORTCUT_CLASSES) {

            String className = classToCast.className;
            String primitiveName = classToCast.primitiveName;
            String alias = classToCast.alias;

            // Generate "optString(key)" method
            simpleShortcuts += TAB + "public @Nullable " + className + " opt" + alias + "(final @NotNull Object key) {" + BR;
            simpleShortcuts += TAB + TAB + "return opt(key, " + className + ".class);" + BR;
            simpleShortcuts += TAB + "}" + BR;
            simpleShortcuts += BR;

            // Generate "optString(key, default)" method
            simpleShortcuts += TAB + "public @Nullable " + className + " opt" + alias + "(final @NotNull Object key, final @NotNull " + className + " defaultValue) {" + BR;
            simpleShortcuts += TAB + TAB + "return opt(key, " + className + ".class, defaultValue);" + BR;
            simpleShortcuts += TAB + "}" + BR;
            simpleShortcuts += BR;

            // Generate "getString(key)" method
            if (primitiveName != null) {
                simpleShortcuts += TAB + "public " + primitiveName + " get" + alias + "(final @NotNull Object key) {" + BR;
            } else {
                simpleShortcuts += TAB + "public @NotNull " + className + " get" + alias + "(final @NotNull Object key) {" + BR;
            }
            simpleShortcuts += TAB + TAB + "return get(key, " + className + ".class);" + BR;
            simpleShortcuts += TAB + "}" + BR;
            simpleShortcuts += BR;
        }
        code = insertGenerated(code, "SIMPLE-SHORTCUTS", simpleShortcuts);

        // List shortcuts
        String listShortcuts = "";
        for (CodeClass classToCast : SHORTCUT_CLASSES) {

            String className = classToCast.className;
            String alias = classToCast.alias;

            // Generate "optListString(key)" method
            listShortcuts += TAB + "public @Nullable List<" + className + "> optListOf" + alias + "(final @NotNull Object key) {" + BR;
            listShortcuts += TAB + TAB + "return optList(key, " + className + ".class);" + BR;
            listShortcuts += TAB + "}" + BR;
            listShortcuts += BR;

            // Generate "getListString(key)" method
            listShortcuts += TAB + "public @NotNull List<" + className + "> getListOf" + alias + "(final @NotNull Object key) {" + BR;
            listShortcuts += TAB + TAB + "return getList(key, " + className + ".class);" + BR;
            listShortcuts += TAB + "}" + BR;
            listShortcuts += BR;
        }

        code = insertGenerated(code, "LIST-SHORTCUTS", listShortcuts);

        String mapShortcuts = "";
        for (CodeClass keyToCast : MAP_KEY_CLASSES) {

            String keyClassName = keyToCast.className;
            String keyAlias = keyToCast.alias;

            for (CodeClass valueToCast : SHORTCUT_CLASSES) {

                String valueClassName = valueToCast.className;
                String valueAlias = valueToCast.alias;

                // Generate "optMapStringObject(key)" method
                mapShortcuts += TAB + "public @Nullable Map<" + keyClassName + ", " + valueClassName + "> optMapOf" + keyAlias + "To" + valueAlias + "(final @NotNull Object key) {" + BR;
                mapShortcuts += TAB + TAB + "return optMap(key, " + keyClassName + ".class, " + valueClassName + ".class);" + BR;
                mapShortcuts += TAB + "}" + BR;
                mapShortcuts += BR;

                // Generate "getMapStringObject(key)" method
                mapShortcuts += TAB + "public @NotNull Map<" + keyClassName + ", " + valueClassName + "> getMapOf" + keyAlias + "To" + valueAlias + "(final @NotNull Object key) {" + BR;
                mapShortcuts += TAB + TAB + "return getMap(key, " + keyClassName + ".class, " + valueClassName + ".class);" + BR;
                mapShortcuts += TAB + "}" + BR;
                mapShortcuts += BR;
            }
        }

        code = insertGenerated(code, "MAP-SHORTCUTS", mapShortcuts);

        File optGetClassFile = new File("./release/src/main/java/acavailhez/optget/OptGet.java");
        if (!optGetClassFile.exists()) {
            optGetClassFile.createNewFile();
        }
        FileUtils.write(optGetClassFile, code, "UTF-8");

        log.info("Generated source code for OptGet");
    }

    private static String insertGenerated(String source, String alias, String generated) {
        int beginAlias = source.indexOf("// GENERATED-BEGIN:" + alias);
        int endAlias = source.indexOf("// GENERATED-END:" + alias);
        if (beginAlias < 0) {
            throw new RuntimeException("Cannot find tag " + "// GENERATED-BEGIN:" + alias);
        }
        if (endAlias < 0) {
            throw new RuntimeException("Cannot find tag " + "// GENERATED-END:" + alias);
        }
        int begin = source.indexOf("\n", beginAlias);
        int end = source.substring(0, endAlias).lastIndexOf("\n");

        source = source.substring(0, begin) +
                BR +
                generated +
                source.substring(end);
        return source;
    }
}
