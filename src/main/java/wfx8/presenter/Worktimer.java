package wfx8.presenter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import wfx8.model.WorkingDay;
import wfx8.util.DateTimeUtil;

import java.time.Duration;
import java.time.LocalTime;

public final class Worktimer implements EventHandler<ActionEvent> {

    private final WorkingDay workingDay;
    private final Label      remainingTimeLabel;

    public Worktimer(WorkingDay workingDay, Label remainingTimeLabel) {
        this.workingDay = workingDay;
        this.remainingTimeLabel = remainingTimeLabel;
    }

    private LocalTime currentRemainingTime;
    private boolean nowIsOvertime;

    @Override
    public void handle(ActionEvent event) {
        calculateCurrentRemainingTime();
        setCurrentRemainingTimeInGUI();
    }

    private void calculateCurrentRemainingTime() {
        LocalTime endTime = workingDay.getEndTime();
        Duration remainingDuration = Duration.between(LocalTime.now(), endTime);
        if(remainingDuration.isNegative()) {
            nowIsOvertime = true;
            currentRemainingTime = LocalTime.MIN.minus(remainingDuration);
        } else {
            nowIsOvertime = false;
            currentRemainingTime = LocalTime.MIN.plus(remainingDuration);
        }
    }

    private void setCurrentRemainingTimeInGUI() {
        Platform.runLater(() -> {
            if(nowIsOvertime) {
                remainingTimeLabel.setText("+" + DateTimeUtil.formatRemainingTime(currentRemainingTime));
            } else {
                remainingTimeLabel.setText("-" + DateTimeUtil.formatRemainingTime(currentRemainingTime));
            }
        });
    }
}
