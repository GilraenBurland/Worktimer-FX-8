package wfx8.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import wfx8.model.WorkingDay;

public final class WorkingDayHelper {

    private WorkingDayHelper() {
    }

    public static WorkingDay loadWorkingDay() throws ReadWriteException {
        WorkingDay workingDay = null;
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

    private static WorkingDay createNewWorkingDay() throws ReadWriteException {
        ZonedDateTime startTime = ZonedDateTime.now();
        LocalTime endTime = LocalTime.now().plus(8, ChronoUnit.HOURS).plus(21, ChronoUnit.MINUTES);
        WorkingDay workingDay = new WorkingDay(startTime, endTime);
        return workingDay;
    }

    public static void serialize(WorkingDay workingDay) throws ReadWriteException {
        File workingDayFile = getWorkingDayFile();
        tryWriteWorkingDay(workingDayFile, workingDay);
    }

    private static File getWorkingDayFile() throws ReadWriteException {
        File worktimerDirectory = getWorktimerDirectory();
        File workingDayFile = new File(worktimerDirectory, "WorkingDay.wfx8");
        return workingDayFile;
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
            bufferedWriter.append("startTime=" + workingDay.getStartTime().toString());
            bufferedWriter.newLine();
            bufferedWriter.append("endTime=" + workingDay.getEndTime().toString());
        }
    }

}
