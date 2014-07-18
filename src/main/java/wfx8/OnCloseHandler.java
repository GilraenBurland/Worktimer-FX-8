package wfx8;

import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.controlsfx.dialog.Dialogs;

import wfx8.model.WorkingDay;
import wfx8.model.WorktimerConfig;
import wfx8.presenter.WorktimerPresenter;
import wfx8.util.ReadWriteException;
import wfx8.util.WorkingDayHelper;
import wfx8.util.WorktimerConfigHelper;

public final class OnCloseHandler implements EventHandler<WindowEvent>{
    
    private final WorktimerPresenter presenter;
    private final Stage primaryStage;
    
    public OnCloseHandler(WorktimerPresenter presenter, Stage primaryStage) {
        this.presenter = presenter;
        this.primaryStage = primaryStage;
    }

    @Override
    public void handle(WindowEvent event) {
        try {
            saveWorkingDay();
            saveConfiguration();
        } catch (Exception e) {
            Dialogs.create().title("Error").message("An error occured.").showException(e);
            e.printStackTrace();
        }
    }

    private void saveWorkingDay() throws ReadWriteException {
        WorkingDay workingDay = presenter.getWorkingDay();
        WorkingDayHelper.serialize(workingDay);
    }

    private void saveConfiguration() throws Exception {
        WorktimerConfig config = WorktimerConfigHelper.getCurrentConfig();
        config.stageX = primaryStage.getX();
        config.stageY = primaryStage.getY();
        WorktimerConfigHelper.saveConfig(config);
    }
}
