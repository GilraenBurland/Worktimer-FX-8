package com.kn.wfx8.presenter;

import java.time.Duration;
import java.time.LocalTime;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;

import com.kn.wfx8.model.WorkingDay;
import com.kn.wfx8.util.DateTimeUtil;

public final class Worktimer implements EventHandler<ActionEvent> {

    private final WorkingDay workingDay;
    private final Label      remainingTimeLabel;

    public Worktimer(WorkingDay workingDay, Label remainingTimeLabel) {
        this.workingDay = workingDay;
        this.remainingTimeLabel = remainingTimeLabel;
    }

    private LocalTime currentRemainingTime;

    @Override
    public void handle(ActionEvent event) {
        calculateCurrentRemainingTime();
        setCurrentRemainingTimeToModelAndGUI();
    }

    private void calculateCurrentRemainingTime() {
        LocalTime endTime = workingDay.getEndTime();
        Duration remainingDuration = Duration.between(LocalTime.now(), endTime);
        currentRemainingTime = (LocalTime) remainingDuration.addTo(LocalTime.of(0, 0, 0));
    }
    
    private void setCurrentRemainingTimeToModelAndGUI() {
        Platform.runLater(() -> remainingTimeLabel.setText(DateTimeUtil.formatRemainingTime(currentRemainingTime))); 
    }
}
