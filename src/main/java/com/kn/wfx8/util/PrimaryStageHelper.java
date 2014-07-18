package com.kn.wfx8.util;

import javafx.stage.Stage;

import com.kn.wfx8.model.WorktimerConfig;

public final class PrimaryStageHelper {

    private PrimaryStageHelper() {}
    
    public static void setStageCoordinates(Stage primaryStage) throws Exception {
        WorktimerConfig config = WorktimerConfigHelper.getCurrentConfig();
        primaryStage.setX(config.stageX);
        primaryStage.setY(config.stageY);
    }
    
}
