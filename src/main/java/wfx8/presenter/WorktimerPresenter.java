package wfx8.presenter;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import wfx8.model.WorkingDay;
import wfx8.util.DateTimeUtil;
import wfx8.util.ReadWriteException;
import wfx8.util.WorkingDayHelper;
import wfx8.view.ConfigDialog;

import com.google.common.eventbus.EventBus;

public final class WorktimerPresenter {

    @FXML
    private Label startTimeLabel;

    @FXML
    private Label endTimeLabel;

    @FXML
    private Label remainingTimeLabel;

    private WorkingDay workingDay;
    private EventBus eventBus;

    public void go(Stage primaryStage) throws ReadWriteException, InterruptedException {
        this.workingDay = WorkingDayHelper.loadWorkingDay();
        createAndRegisterOnEventBus();
        configureLabels();
        startTimer();
        primaryStage.show();
    }

    private void createAndRegisterOnEventBus() {
        this.eventBus = new EventBus();
        this.eventBus.register(this);
    }

    private void configureLabels() {
        configureStartTimeLabel();
        configureEndTimeLabel();
    }

    private void configureStartTimeLabel() {
        startTimeLabel.setText(DateTimeUtil.formatGeneralTime(workingDay.getStartTime()));
        ObjectProperty<ZonedDateTime> startTimeProperty = workingDay.startTimeProperty();
        startTimeProperty.addListener((observable, oldValue, newValue) ->
                                              startTimeLabel.setText(DateTimeUtil.formatGeneralTime(newValue)));
    }

    private void configureEndTimeLabel() {
        endTimeLabel.setText(DateTimeUtil.formatGeneralTime(workingDay.getEndTime()));
        ObjectProperty<LocalTime> endTimeProperty = workingDay.endTimeProperty();
        endTimeProperty.addListener((observable, oldValue, newValue) ->
                                            endTimeLabel.setText(DateTimeUtil.formatGeneralTime(newValue)));
    }

    private void startTimer() throws InterruptedException {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame timer = new KeyFrame(javafx.util.Duration.seconds(1), (event) -> this.handleKeyFrame());
        timeline.getKeyFrames().add(timer);
        timeline.play();

        Thread.sleep(1000);
    }

    public void handleKeyFrame() {
        calculateCurrentRemainingTime();
        setCurrentRemainingTimeInGUI();
    }
    
    private LocalTime currentRemainingTime;
    private boolean   nowIsOvertime;
    
    private void calculateCurrentRemainingTime() {
        LocalTime endTime = workingDay.getEndTime();
        Duration remainingDuration = Duration.between(LocalTime.now(), endTime);
        if (remainingDuration.isNegative()) {
            nowIsOvertime = true;
            currentRemainingTime = LocalTime.MIN.minus(remainingDuration);
        } else {
            nowIsOvertime = false;
            currentRemainingTime = LocalTime.MIN.plus(remainingDuration);
        }
    }

    private void setCurrentRemainingTimeInGUI() {
        Platform.runLater(() -> {
            if (nowIsOvertime) {
                remainingTimeLabel.setText("+" + DateTimeUtil.formatRemainingTime(currentRemainingTime));
            } else {
                remainingTimeLabel.setText("-" + DateTimeUtil.formatRemainingTime(currentRemainingTime));
            }
        });
    }
    
    public WorkingDay getWorkingDay() {
        return this.workingDay;
    }

    // Implement handlerMethods
    public void openConfigDialog() throws ReadWriteException {
        ConfigDialog dialog = new ConfigDialog(workingDay);
        dialog.showAndWait();
    }
    
}