package wfx8.util;

import wfx8.model.WorktimerConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class WorktimerConfigHelper {

    private static final String STAGE_X = "stageConfig.x";
    private static final String STAGE_Y = "stageConfig.y";

    private static final String START_TIME_OFFSET = "generalConfig.startTimeOffset";
    private static final String WORKING_TIME      = "generalConfig.workingTime";
    private static final String BREAK_TIME        = "generalConfig.breakTime";

    private WorktimerConfigHelper() {
    }

    private static WorktimerConfig currentConfig;

    public static WorktimerConfig getCurrentConfig() throws ReadWriteException {
        if (currentConfig == null) {
            File worktimerConfigFile = getConfigFile();
            Map<String, String> configProperties = readPropertiesFrom(worktimerConfigFile);

            currentConfig = new WorktimerConfig();
            currentConfig.stageConfig.x = Double.valueOf(configProperties.get(STAGE_X));
            currentConfig.stageConfig.y = Double.valueOf(configProperties.get(STAGE_Y));
            currentConfig.generalConfig.startTimeOffset = Duration.parse(configProperties.get(START_TIME_OFFSET));
            currentConfig.generalConfig.workingTime = Duration.parse(configProperties.get(WORKING_TIME));
            currentConfig.generalConfig.breakTime = Duration.parse(configProperties.get(BREAK_TIME));
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
                bufferedWriter.append(STAGE_X + "=0.0");
                bufferedWriter.newLine();
                bufferedWriter.append(STAGE_Y + "=0.0");
                bufferedWriter.newLine();
                bufferedWriter.write(START_TIME_OFFSET + "=" + Duration.ZERO);
                bufferedWriter.newLine();
                bufferedWriter.write(WORKING_TIME + "=" + Duration.ofHours(7).plusMinutes(36));
                bufferedWriter.newLine();
                bufferedWriter.write(BREAK_TIME + "=" + Duration.ofMinutes(45));
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

    private static Map<String, String> readPropertiesFrom(File file) throws ReadWriteException {
        Map<String, String> properties = new HashMap<>();

        try {
            FileReader fileReader = new FileReader(file);
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                String line;
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
        Map<String, String> configProperties = convertToProperties(config);
        try {
            FileWriter fileWriter = new FileWriter(getConfigFile());
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                Iterator<Entry<String, String>> it = configProperties.entrySet().iterator();

                while (it.hasNext()) {
                    Entry<String, String> entry = it.next();

                    bufferedWriter.append(entry.getKey()).append("=").append(entry.getValue());

                    if (it.hasNext()) {
                        bufferedWriter.newLine();
                    }
                }
            }
        } catch (Throwable e) {
            throw new ReadWriteException(e.getMessage());
        }
    }

    private static Map<String, String> convertToProperties(WorktimerConfig config) throws Exception {
        Map<String, String> properties = new HashMap<>();

        properties.put("stageConfig.x", String.valueOf(config.stageConfig.x));
        properties.put("stageConfig.y", String.valueOf(config.stageConfig.y));
        properties.put("generalConfig.startTimeOffset", config.generalConfig.startTimeOffset.toString());
        properties.put("generalConfig.workingTime", config.generalConfig.workingTime.toString());
        properties.put("generalConfig.breakTime", config.generalConfig.breakTime.toString());

        return properties;
    }
}
