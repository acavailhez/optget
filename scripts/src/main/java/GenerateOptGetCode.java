import acavailhez.optget.OptGet;
import acavailhez.optget.OptGetMap;
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
    private final static List<Class> SHORTCUT_CLASSES = new ArrayList<>();

    // All classes that will have shortcuts in the map utils, as a key
    private final static List<Class> MAP_KEY_CLASSES = new ArrayList<>();

    private final static String BR = System.lineSeparator();
    private final static String TAB = "    ";

    public static void main(String[] args) throws IOException {
        log.info("hello");
        log.info("Generating source code for OptGet");

        SHORTCUT_CLASSES.add(String.class);
        SHORTCUT_CLASSES.add(Byte.class);
        SHORTCUT_CLASSES.add(Short.class);
        SHORTCUT_CLASSES.add(Integer.class);
        SHORTCUT_CLASSES.add(Long.class);
        SHORTCUT_CLASSES.add(Float.class);
        SHORTCUT_CLASSES.add(Double.class);
        SHORTCUT_CLASSES.add(OptGet.class);
        SHORTCUT_CLASSES.add(OptGetMap.class);

        MAP_KEY_CLASSES.add(String.class);
        MAP_KEY_CLASSES.add(Integer.class);
        MAP_KEY_CLASSES.add(Long.class);
        MAP_KEY_CLASSES.add(Float.class);
        MAP_KEY_CLASSES.add(Double.class);

        File model = new File("./release/src/main/java/acavailhez/optget/OptGet.java");
        String code = FileUtils.readFileToString(model, "UTF-8");


        String code2 = insertGenerated(code, "SIMPLE-SHORTCUTS", "test");

        // Simple shortcuts
        String simpleShortcuts = "";
        for (Class classToCast : SHORTCUT_CLASSES) {

            String name = classToCast.getSimpleName();

            // Generate "optString(key)" method
            simpleShortcuts += TAB + " " + name + " opt" + name + "(Object key) {" + BR;
            simpleShortcuts += TAB + TAB + "return opt(key, " + name + ".class);" + BR;
            simpleShortcuts += TAB + "}" + BR;
            simpleShortcuts += BR;

            // Generate "optString(key, default)" method
            simpleShortcuts += TAB + " " + name + " opt" + name + "(Object key, " + name + " defaultValue) {" + BR;
            simpleShortcuts += TAB + TAB + "return opt(key, " + name + ".class, defaultValue);" + BR;
            simpleShortcuts += TAB + "}" + BR;
            simpleShortcuts += BR;

            // Generate "getString(key)" method
            simpleShortcuts += TAB + " " + name + " get" + name + "(Object key) {" + BR;
            simpleShortcuts += TAB + TAB + "return get(key, " + name + ".class);" + BR;
            simpleShortcuts += TAB + "}" + BR;
            simpleShortcuts += BR;
        }
        code = insertGenerated(code, "SIMPLE-SHORTCUTS", simpleShortcuts);

        // List shortcuts
        String listShortcuts = "";
        for (Class classToCast : SHORTCUT_CLASSES) {

            String name = classToCast.getSimpleName();

            // Generate "optListString(key)" method
            listShortcuts += TAB + " List<" + name + "> optList" + name + "(Object key) {" + BR;
            listShortcuts += TAB + TAB + "return optList(key, " + name + ".class);" + BR;
            listShortcuts += TAB + "}" + BR;
            listShortcuts += BR;

            // Generate "getListString(key)" method
            listShortcuts += TAB + " List<" + name + "> getList" + name + "(Object key) {" + BR;
            listShortcuts += TAB + TAB + "return getList(key, " + name + ".class);" + BR;
            listShortcuts += TAB + "}" + BR;
            listShortcuts += BR;
        }

        code = insertGenerated(code, "LIST-SHORTCUTS", listShortcuts);

        String mapShortcuts = "";
        for (Class keyToCast : MAP_KEY_CLASSES) {
            String key = keyToCast.getSimpleName();
            for (Class valueToCast : SHORTCUT_CLASSES) {
                String value = valueToCast.getSimpleName();

                // Generate "optMapStringObject(key)" method
                mapShortcuts += TAB + " Map<" + key + ", " + value + "> optMap" + key + value + "(Object key) {" + BR;
                mapShortcuts += TAB + TAB + "return optMap(key, " + key + ".class, " + value + ".class);" + BR;
                mapShortcuts += TAB + "}" + BR;
                mapShortcuts += BR;

                // Generate "getMapStringObject(key)" method
                mapShortcuts += TAB + " Map<" + key + ", " + value + "> getMap" + key + value + "(Object key) {" + BR;
                mapShortcuts += TAB + TAB + "return getMap(key, " + key + ".class, " + value + ".class);" + BR;
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
                generated +
                source.substring(end);
        return source;
    }
}
