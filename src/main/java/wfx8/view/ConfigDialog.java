package wfx8.view;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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

public final class ConfigDialog extends Stage implements Initializable {

    @FXML
    private Accordion        accordion;

    private final WorkingDay workingDay;

    public ConfigDialog(WorkingDay workingDay) throws ReadWriteException {
        this.workingDay = workingDay;
        configureDialog();
        loadStageContent();
    }

    private void configureDialog() throws ReadWriteException {
        setTitle("Config Dialog");
        setMinHeight(200);
        setPosition();
    }
    
    private void setPosition() throws ReadWriteException {
        WorktimerConfig config = WorktimerConfigHelper.getCurrentConfig();
        setX(config.stageX);
        setY(config.stageY + 120);
    }

    private void loadStageContent() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ConfigDialog.fxml"));
        fxmlLoader.setController(this);
        trySetSceneFrom(fxmlLoader);
    }

    private void trySetSceneFrom(FXMLLoader fxmlLoader) {
        try {
            setScene(new Scene((Parent) fxmlLoader.load()));
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
    private Label           dailyOffsetLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        expandFirstTitledPane();
        initializeStartTime();
        initializeEndTime();
        calculateAndSetDailyOffset();
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
        calculateAndSetEndTime();
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

    private void calculateAndSetEndTime() {
        endTime.setLocalTime(getStandardEndTime());
    }

    private void calculateAndSetDailyOffset() {
        LocalTime standardEndTime = getStandardEndTime();
        LocalTime workingDayEndTime = workingDay.getEndTime();
        String newDailyOffset = generateDailyOffsetString(standardEndTime, workingDayEndTime);
        updateDailyOffsetWith(standardEndTime, workingDayEndTime, newDailyOffset);
    }

    private static String generateDailyOffsetString(LocalTime standardEndTime, LocalTime workingDayEndTime) {
        int hourOffset = Math.abs(workingDayEndTime.getHour() - standardEndTime.getHour());
        int minuteOffset = Math.abs(workingDayEndTime.getMinute() - standardEndTime.getMinute());
        String newDailyOffset = hourOffset + ":" + minuteOffset;
        return newDailyOffset;
    }

    private void updateDailyOffsetWith(LocalTime standardEndTime, LocalTime workingDayEndTime, String newDailyOffset) {
        if (workingDayEndTime.isBefore(standardEndTime)) {
            dailyOffsetLabel.setText('-' + newDailyOffset);
        } else {
            dailyOffsetLabel.setText(newDailyOffset);
        }
    }

    private LocalTime getStandardEndTime() {
        return workingDay.getStartTime().toLocalTime().plus(8, ChronoUnit.HOURS).plus(21, ChronoUnit.MINUTES);
    }

}