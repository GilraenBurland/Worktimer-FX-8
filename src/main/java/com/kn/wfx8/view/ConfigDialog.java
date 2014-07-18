package com.kn.wfx8.view;

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

import com.kn.wfx8.model.WorkingDay;

public class ConfigDialog extends Stage implements Initializable {

    @FXML
    private Accordion        accordion;

    private final WorkingDay workingDay;

    public ConfigDialog(WorkingDay workingDay) {
        this.workingDay = workingDay;

        setTitle("Config Dialog");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ConfigDialog.fxml"));
        fxmlLoader.setController(this);

        try {
            setScene(new Scene((Parent) fxmlLoader.load()));
            setMinHeight(200);
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
        accordion.setExpandedPane(accordion.getPanes().get(0));

        startTime.withLocalTime(LocalTime
                .of(workingDay.getStartTime().getHour(), workingDay.getStartTime().getMinute()));
        startTime.localTimeProperty().addListener((observable, oldValue, newValue) -> {
            ZonedDateTime newStartTime = workingDay.getStartTime().with(newValue);
            workingDay.startTimeProperty().set(newStartTime);
            calculateAndSetEndTime();
            calculateAndSetDailyOffset();
        });

        endTime.withLocalTime(workingDay.getEndTime());
        endTime.localTimeProperty().addListener((observable, oldValue, newValue) -> {
            workingDay.endTimeProperty().set(newValue);
            calculateAndSetDailyOffset();
        });

        calculateAndSetDailyOffset();
    }

    private void calculateAndSetEndTime() {
        endTime.setLocalTime(getStandardEndTime());
    }

    private void calculateAndSetDailyOffset() {
        LocalTime standardEndTime = getStandardEndTime();
        LocalTime workingDayEndTime = workingDay.getEndTime();
        
        int hourOffset = workingDayEndTime.getHour() - standardEndTime.getHour();
        int minuteOffset = workingDayEndTime.getMinute() - standardEndTime.getMinute();
        
        String newDailyOffset = Math.abs(hourOffset) + ":" + Math.abs(minuteOffset);
        
        if(workingDayEndTime.isBefore(standardEndTime)) {
            dailyOffsetLabel.setText('-' + newDailyOffset);
        } else {
            dailyOffsetLabel.setText(newDailyOffset);
        }
    }

    private LocalTime getStandardEndTime() {
        return workingDay.getStartTime().toLocalTime().plus(8, ChronoUnit.HOURS).plus(21, ChronoUnit.MINUTES);
    }

}