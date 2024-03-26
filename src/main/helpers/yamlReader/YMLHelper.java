package helpers.yamlReader;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YMLHelper {

    public static String browserDefault;
    public static boolean withMobileEmulation;
    public static boolean runHeadless;
    public static boolean tearDownEnabled;
    public static boolean forceMaximize;
    public static LinkedHashMap<String, Object> yamlData;
    // Define config file
    private static String folderPath = System.getProperty("user.dir")
            + File.separator + "src" + File.separator + "main" + File.separator + "config" + File.separator;
    private static String filePath = folderPath + "config.yaml";

    public static void setProperties() {
        try {
            // Load the YAML file into a nested LinkedHashMap directly

            InputStream ymlInputStream = Files.newInputStream(Paths.get(filePath));

            Yaml yaml = new Yaml();
            yamlData = yaml.load(ymlInputStream);

            // Apply configuration
            browserDefault = YMLHelper.getYMLValue(YMLHelper.yamlData, "browser.default");
            withMobileEmulation = Boolean.TRUE.equals(YMLHelper.getYMLValue(YMLHelper.yamlData, "browser.mobile"));
            runHeadless = Boolean.TRUE.equals(YMLHelper.getYMLValue(YMLHelper.yamlData, "browser.headless"));
            tearDownEnabled = Boolean.TRUE.equals(YMLHelper.getYMLValue(YMLHelper.yamlData, "browser.tearDown"));
            forceMaximize = Boolean.TRUE.equals(YMLHelper.getYMLValue(YMLHelper.yamlData, "browser.maximize"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T getYMLValue(Map<String, Object> map, String key) {
        String[] keys = key.split("\\.");
        for (int i = 0; i < keys.length - 1; i++) {
            if (map == null) {
                return null;
            }
            map = (Map<String, Object>) map.get(keys[i]);
        }
        if (map == null) {
            return null;
        }
        return (T) map.get(keys[keys.length - 1]);
    }

    public static void updateYMLChildlessValue(String key, Object value) {
        try {
            // Split the key into individual parts
            String[] keys = key.split("\\.");

            // Get the parent node based on the first part of the key
            Map<String, Object> parentNode = yamlData;
            for (int i = 0; i < keys.length - 1; i++) {
                if (parentNode.containsKey(keys[i])) {
                    parentNode = (Map<String, Object>) parentNode.get(keys[i]);
                } else {
                    throw new IllegalArgumentException("Key not found: " + key);
                }
            }

            // Update the value of the final key
            parentNode.put(keys[keys.length - 1], value);

            // Write the changes back to the YAML file
            writeYamlToFile(key, value.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeYamlToFile(String key, String newValue) {
        try {
            // Create a temporary file
            Path tempFilePath = Paths.get(filePath + ".temp");
            BufferedWriter writer = Files.newBufferedWriter(tempFilePath);

            // Read the existing YAML content with comments
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            // Flag to track if the key has been found
            boolean foundKey = false;

            // Iterate through each line of the YAML content
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                // Check if the line contains the key
                if (line.trim().startsWith(key + ":")) {
                    // Replace the value with the new value
                    writer.write(key + ": " + newValue);
                    writer.newLine();
                    foundKey = true;
                } else {
                    // Copy the original line
                    writer.write(line);
                    // If it's not the last line, add a newline character
                    if (i < lines.size() - 1) {
                        writer.newLine();
                    }
                }
            }

            // If the key was not found, append it at the end
            if (!foundKey) {
                writer.write(key + ": " + newValue);
                writer.newLine();
            }

            // Close the writer
            writer.close();

            // Replace the original file with the temporary file
            Files.move(tempFilePath, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeYamlToFileWithoutComments() {
        try {
            // Create a FileWriter to write to the YAML file
            FileWriter fileWriter = new FileWriter(filePath);

            // Create DumperOptions to configure YAML output
            DumperOptions options = new DumperOptions();
            options.setIndent(2); // Set indentation level to 2 spaces
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Use block style

            // Create a Yaml instance with configured options
            Yaml yaml = new Yaml(options);

            // Dump YAML data to the file using FileWriter
            yaml.dump(yamlData, fileWriter);

            // Close the FileWriter
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// nested

    public static void updateYMLValue(String key, Object newValue) {
        try {
            // Split the key into individual parts
            String[] keys = key.split("\\.");
            // Load the existing YAML content into a List of Strings
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            // Create a temporary file
            Path tempFilePath = Paths.get(filePath + ".temp");
            BufferedWriter writer = Files.newBufferedWriter(tempFilePath);

            // Variables to keep track of the current node and indentation
            String currentIndent = "";
            int keyIndex = 0;

            // Iterate through each line of the YAML content
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                // Check if the line is a comment or empty
                if (line.trim().startsWith("#") || line.trim().isEmpty()) {
                    writer.write(line);
                    if (i < lines.size() - 1) {
                        writer.newLine();
                    }
                    continue;
                }

                // Extract the current line's key and indentation
                String trimmedLine = line.trim();
                String currentLineIndent = line.substring(0, line.indexOf(trimmedLine));
                String currentLineKey = trimmedLine.split(":")[0];

                // Check if the current line's key matches the target key at the current depth
                if (currentLineKey.equals(keys[keyIndex]) && currentLineIndent.length() == currentIndent.length()) {
                    // If we are at the last key, update the value
                    if (keyIndex == keys.length - 1) {
                        writer.write(currentLineIndent + keys[keyIndex] + ": " + newValue);
                        writer.newLine();
                        // Skip the original value line
                        continue;
                    } else {
                        // Move to the next key and update the indentation
                        keyIndex++;
                        currentIndent = "  " + currentIndent; // Assuming 2 spaces per indentation level
                    }
                } else if (currentLineIndent.length() < currentIndent.length()) {
                    // If the indentation decreases, reset to the previous level
                    keyIndex--;
                    currentIndent = currentIndent.substring(2);
                }

                // Write the original line
                writer.write(line);
                // Only add a newline if it's not the last line
                if (i < lines.size() - 1) {
                    writer.newLine();
                }
            }

            // Close the writer
            writer.close();

            // Replace the original file with the temporary file
            Files.move(tempFilePath, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }
}