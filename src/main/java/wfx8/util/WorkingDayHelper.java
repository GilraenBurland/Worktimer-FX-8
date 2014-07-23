package wfx8.util;

import wfx8.model.GeneralConfig;
import wfx8.model.WorkingDay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Properties;

public final class WorkingDayHelper {

    private WorkingDayHelper() {
    }

    public static WorkingDay loadWorkingDay() throws ReadWriteException {
        WorkingDay workingDay;
        try {
            File workingDayFile = getWorkingDayFile();
            workingDay = createWorkingDayFrom(workingDayFile);

            ZonedDateTime now = ZonedDateTime.now();
            if (now.getDayOfMonth() > workingDay.getStartTime().getDayOfMonth()) {
                workingDay = createNewWorkingDay();
                tryWriteWorkingDay(workingDayFile, workingDay);
            }
        } catch (ReadWriteException e) {
            workingDay = createNewWorkingDay();
            serialize(workingDay);
        }

        return workingDay;
    }

    private static WorkingDay createWorkingDayFrom(File workingDayFile) throws ReadWriteException {
        Properties properties = readPropertiesFrom(workingDayFile);
        ZonedDateTime startTime = ZonedDateTime.parse(properties.getProperty("startTime"));
        LocalTime endTime = LocalTime.parse(properties.getProperty("endTime"));
        return new WorkingDay(startTime, endTime);
    }

    private static Properties readPropertiesFrom(File file) throws ReadWriteException {
        Properties properties = new Properties();

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

    private static WorkingDay createNewWorkingDay() throws ReadWriteException {
        GeneralConfig generalConfig = WorktimerConfigHelper.getCurrentConfig().generalConfig;
        ZonedDateTime startTime = ZonedDateTime.now().minus(generalConfig.startTimeOffset);
        LocalTime endTime = startTime.toLocalTime().plus(generalConfig.workingTime).plus(generalConfig.breakTime);
        return new WorkingDay(startTime, endTime);
    }

    public static void serialize(WorkingDay workingDay) throws ReadWriteException {
        File workingDayFile = getWorkingDayFile();
        tryWriteWorkingDay(workingDayFile, workingDay);
    }

    private static File getWorkingDayFile() throws ReadWriteException {
        File worktimerDirectory = getWorktimerDirectory();
        return new File(worktimerDirectory, "WorkingDay.wfx8");
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

    private static void tryWriteWorkingDay(File workingDayFile, WorkingDay workingDay) throws ReadWriteException {
        try {
            writeWorkingDay(workingDayFile, workingDay);
        } catch (IOException e) {
            throw new ReadWriteException(e.getMessage());
        }
    }

    private static void writeWorkingDay(File workingDayFile, WorkingDay workingDay) throws IOException {
        FileWriter fileWriter = new FileWriter(workingDayFile);

        try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.append("startTime=").append(workingDay.getStartTime().toString());
            bufferedWriter.newLine();
            bufferedWriter.append("endTime=").append(workingDay.getEndTime().toString());
        }
    }

}
