package wfx8.util;

import wfx8.model.WorktimerConfig;
import javafx.stage.Stage;

public final class PrimaryStageHelper {

    private PrimaryStageHelper() {}
    
    public static void setStageCoordinates(Stage primaryStage) throws Exception {
        WorktimerConfig config = WorktimerConfigHelper.getCurrentConfig();
        primaryStage.setX(config.stageX);
        primaryStage.setY(config.stageY);
    }
    
}
