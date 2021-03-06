package wfx8;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import org.controlsfx.dialog.Dialogs;

import wfx8.model.WorkingDay;
import wfx8.model.WorktimerConfig;
import wfx8.presenter.WorktimerPresenter;
import wfx8.util.ReadWriteException;
import wfx8.util.WorkingDayHelper;
import wfx8.util.WorktimerConfigHelper;

public final class OnCloseHandler implements EventHandler<WindowEvent> {

    private final WorktimerPresenter presenter;

    public OnCloseHandler(WorktimerPresenter presenter) {
        this.presenter = presenter;
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

    private static void saveConfiguration() throws Exception {
        WorktimerConfig config = WorktimerConfigHelper.getCurrentConfig();
        WorktimerConfigHelper.saveConfig(config);
    }
}
