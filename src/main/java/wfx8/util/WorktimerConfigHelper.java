package wfx8.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import wfx8.model.WorktimerConfig;

public final class WorktimerConfigHelper {

    private WorktimerConfigHelper() {
    }

    private static WorktimerConfig currentConfig;

    public static WorktimerConfig getCurrentConfig() throws ReadWriteException {
        if (currentConfig == null) {
            File worktimerConfigFile = getConfigFile();
            Properties configProperties = readPropertiesFrom(worktimerConfigFile);

            currentConfig = new WorktimerConfig();
            currentConfig.stageX = Double.valueOf((String) configProperties.get("stageX"));
            currentConfig.stageY = Double.valueOf((String) configProperties.get("stageY"));
        }
        return currentConfig;
    }

    private static File getConfigFile() throws ReadWriteException {
        File worktimerDirectory = getWorktimerDirectory();
        File configFile = new File(worktimerDirectory, "Config.wfx8");

        if (!configFile.exists()) {
            createDefaultsIn(configFile);
        }

        return configFile;
    }

    private static void createDefaultsIn(File configFile) throws ReadWriteException {
        try {
            FileWriter fileWriter = new FileWriter(configFile);

            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.append("stageX=0.0");
                bufferedWriter.newLine();
                bufferedWriter.append("stageY=0.0");
            }
        } catch (IOException e) {
            throw new ReadWriteException(e.getMessage());
        }
    }

    private static File getWorktimerDirectory() throws ReadWriteException {
        File worktimerDirectory = new File(System.getProperty("user.home") + "/WorkTimer");

        if (!worktimerDirectory.exists()) {
            if (!worktimerDirectory.mkdir()) {
                throw new ReadWriteException("Failed to create Worktimer Directory");
            }
        }

        return worktimerDirectory;
    }

    private static Properties readPropertiesFrom(File file) throws ReadWriteException {
        Properties properties = new Properties();

        try {
            FileReader fileReader = new FileReader(file);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] property = line.split("=");
                    properties.put(property[0], property[1]);
                }
            }
        } catch (IOException e) {
            throw new ReadWriteException(e.getMessage());
        }

        return properties;
    }

    public static void saveConfig(WorktimerConfig config) throws Exception {
        Properties configProperties = convertToProperties(config);
        try {
            FileWriter fileWriter = new FileWriter(getConfigFile());
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                Iterator<Entry<Object, Object>> it = configProperties.entrySet().iterator();

                while (it.hasNext()) {
                    Entry<Object, Object> entry = it.next();

                    bufferedWriter.append(entry.getKey() + "=" + entry.getValue());

                    if (it.hasNext()) {
                        bufferedWriter.newLine();
                    }
                }
            }
        } catch (Throwable e) {
            throw new ReadWriteException(e.getMessage());
        }
    }

    private static final Properties convertToProperties(WorktimerConfig config) throws Exception {
        Properties properties = new Properties();

        Field[] fields = WorktimerConfig.class.getFields();

        for (Field field : fields) {
            writePropertyFrom(field, config, properties);
        }

        return properties;
    }

    private static void writePropertyFrom(Field field, WorktimerConfig config, Properties properties) throws Exception {
        String fieldName = field.getName();
        Object value = field.get(config);
        properties.put(fieldName, value);
    }

}
