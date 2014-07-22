package wfx8.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalTimePicker;
import org.controlsfx.dialog.Dialogs;
import wfx8.model.WorkingDay;
import wfx8.model.WorktimerConfig;
import wfx8.util.ReadWriteException;
import wfx8.util.WorktimerConfigHelper;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

public final class ConfigDialog extends Stage implements Initializable {

    @FXML
    private Accordion accordion;

    private final WorkingDay workingDay;

    private final WorktimerConfig config;

    public ConfigDialog(WorkingDay workingDay) throws ReadWriteException {
        this.workingDay = workingDay;
        this.config = WorktimerConfigHelper.getCurrentConfig();

        configureDialog();
        loadStageContent();
    }

    private void configureDialog() throws ReadWriteException {
        setTitle("Config Dialog");
        setMinHeight(200);
        setPosition();
    }

    private void setPosition() throws ReadWriteException {
        setX(config.stageConfig.x);
        setY(config.stageConfig.y + 120);
    }

    private void loadStageContent() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ConfigDialog.fxml"));
        fxmlLoader.setController(this);
        trySetSceneFrom(fxmlLoader);
    }

    private void trySetSceneFrom(FXMLLoader fxmlLoader) {
        try {
            setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
            Dialogs.create().title("Error").message("Failed to open Config Dialog.").showException(e);
        }
    }

    @FXML
    private LocalTimePicker startTime;

    @FXML
    private LocalTimePicker endTime;

    @FXML
    private Label dailyOffsetLabel;

    @FXML
    private LocalTimePicker startTimeOffset;

    @FXML
    private LocalTimePicker workingTime;

    @FXML
    private LocalTimePicker breakTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        expandFirstTitledPane();
        initializeStartTime();
        initializeEndTime();
        calculateAndSetDailyOffset();
        initializeStartTimeOffset();
        initializeWorkingTime();
        initializeBreakTime();
    }

    private void expandFirstTitledPane() {
        accordion.setExpandedPane(accordion.getPanes().get(0));
    }

    private void initializeStartTime() {
        LocalTime currentStartTime = LocalTime.of(workingDay.getStartTime().getHour(), workingDay.getStartTime()
                                                                                                 .getMinute());
        startTime.withLocalTime(currentStartTime);
        startTime.localTimeProperty()
                 .addListener((observable, oldValue, newValue) -> doHandleStartTimeChangedEvent(newValue));
    }

    private void doHandleStartTimeChangedEvent(LocalTime newValue) {
        ZonedDateTime newStartTime = workingDay.getStartTime().with(newValue);
        workingDay.setStartTime(newStartTime);
        calculateAndSetStandardEndTime();
        calculateAndSetDailyOffset();
    }

    private void initializeEndTime() {
        endTime.withLocalTime(workingDay.getEndTime());
        endTime.localTimeProperty()
               .addListener((observable, oldValue, newValue) -> doHandleEndTimeChangedEvent(newValue));
    }

    private void doHandleEndTimeChangedEvent(LocalTime newValue) {
        workingDay.setEndTime(newValue);
        calculateAndSetDailyOffset();
    }

    private void calculateAndSetStandardEndTime() {
        LocalTime newEndTime = calculateStandardEndTime();
        endTime.setLocalTime(newEndTime);
    }

    private void calculateAndSetDailyOffset() {
        LocalTime standardEndTime = calculateStandardEndTime();
        LocalTime workingDayEndTime = workingDay.getEndTime();
        String newDailyOffset = generateDailyOffsetString(standardEndTime, workingDayEndTime);
        updateDailyOffsetWith(standardEndTime, workingDayEndTime, newDailyOffset);
    }

    private LocalTime calculateStandardEndTime() {
        return workingDay.getStartTime()
                         .toLocalTime()
                         .plus(config.generalConfig.workingTime)
                         .plus(config.generalConfig.breakTime);
    }

    private static String generateDailyOffsetString(LocalTime standardEndTime, LocalTime workingDayEndTime) {
        Duration dailyOffset = Duration.between(standardEndTime, workingDayEndTime).abs();
        long offsetHours = dailyOffset.toHours();
        long offsetMinutes = dailyOffset.toMinutes() - (offsetHours * 60);
        return offsetHours + ":" + offsetMinutes;
    }

    private void updateDailyOffsetWith(LocalTime standardEndTime, LocalTime workingDayEndTime, String newDailyOffset) {
        if (workingDayEndTime.isBefore(standardEndTime)) {
            dailyOffsetLabel.setText('-' + newDailyOffset);
        } else {
            dailyOffsetLabel.setText(newDailyOffset);
        }
    }

    private void initializeStartTimeOffset() {
        startTimeOffset.withLocalTime(LocalTime.MIN.plus(config.generalConfig.startTimeOffset));
        startTimeOffset.localTimeProperty()
                       .addListener((observable, oldValue, newValue) -> doHandleStartTimeOffsetChangedEvent(newValue));
    }

    private void doHandleStartTimeOffsetChangedEvent(LocalTime newValue) {
        config.generalConfig.startTimeOffset = Duration.between(LocalTime.MIN, newValue);
    }

    private void initializeWorkingTime() {
        workingTime.withLocalTime(LocalTime.MIN.plus(config.generalConfig.workingTime));
        workingTime.localTimeProperty()
                   .addListener((observable, oldValue, newValue) -> doHandleWorkingTimeChangedEvent(newValue));
    }

    private void doHandleWorkingTimeChangedEvent(LocalTime newValue) {
        config.generalConfig.workingTime = Duration.between(LocalTime.MIN, newValue);
        calculateAndSetStandardEndTime();
    }

    private void initializeBreakTime() {
        breakTime.withLocalTime(LocalTime.MIN.plus(config.generalConfig.breakTime));
        breakTime.localTimeProperty()
                 .addListener((observable, oldValue, newValue) -> doHandleBreakTimeChangedEvent(newValue));
    }

    private void doHandleBreakTimeChangedEvent(LocalTime newValue) {
        config.generalConfig.breakTime = Duration.between(LocalTime.MIN, newValue);
        calculateAndSetStandardEndTime();
    }

}