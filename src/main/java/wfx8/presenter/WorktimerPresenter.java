package wfx8.presenter;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import wfx8.model.WorkingDay;
import wfx8.util.DateTimeUtil;
import wfx8.util.ReadWriteException;
import wfx8.util.WorkingDayHelper;
import wfx8.view.ConfigDialog;

import java.time.LocalTime;
import java.time.ZonedDateTime;

public final class WorktimerPresenter {

    @FXML
    private Label startTimeLabel;

    @FXML
    private Label endTimeLabel;

    @FXML
    private Label remainingTimeLabel;

    private WorkingDay workingDay;

    public void go(Stage primaryStage) throws ReadWriteException, InterruptedException {
        this.workingDay = WorkingDayHelper.loadWorkingDay();
        configureLabels();
        startTimer();
        primaryStage.show();
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

        KeyFrame timer = new KeyFrame(javafx.util.Duration.seconds(1), new Worktimer(workingDay, remainingTimeLabel));
        timeline.getKeyFrames().add(timer);
        timeline.play();

        Thread.sleep(1000);
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