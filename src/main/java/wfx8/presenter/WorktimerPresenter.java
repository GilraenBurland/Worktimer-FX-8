package wfx8.presenter;

import wfx8.model.WorkingDay;
import wfx8.util.DateTimeUtil;
import wfx8.util.ReadWriteException;
import wfx8.util.WorkingDayHelper;
import wfx8.view.ConfigDialog;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WorktimerPresenter {

    @FXML
    private Label      startTimeLabel;

    @FXML
    private Label      endTimeLabel;

    @FXML
    private Label      remainingTimeLabel;

    private WorkingDay workingDay;

    public void go(Stage primaryStage) throws ReadWriteException, InterruptedException {
        this.workingDay = WorkingDayHelper.loadWorkingDay();

        startTimeLabel.setText(DateTimeUtil.formatGeneralTime(workingDay.getStartTime()));
        workingDay.startTimeProperty().addListener((observable, oldValue, newValue) ->
            startTimeLabel.setText(DateTimeUtil.formatGeneralTime(newValue))
        );
        
        endTimeLabel.setText(DateTimeUtil.formatGeneralTime(workingDay.getEndTime()));
        workingDay.endTimeProperty().addListener((observable, oldValue, newValue) ->
            endTimeLabel.setText(DateTimeUtil.formatGeneralTime(newValue))
        );

        startTimer();
        primaryStage.show();
    }
    
    private Timeline timeline;

    private void startTimer() throws InterruptedException {
        timeline = new Timeline();
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
    public void openConfigDialog() {
        ConfigDialog dialog = new ConfigDialog(workingDay);
        dialog.showAndWait();
    }
}