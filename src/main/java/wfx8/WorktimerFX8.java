package wfx8;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wfx8.model.WorktimerConfig;
import wfx8.presenter.WorktimerPresenter;
import wfx8.util.WorktimerConfigHelper;

import java.net.URL;

public final class WorktimerFX8 extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Worktimer FX 8");
        setStageCoordinates();
        addStageCoordinatesListener();
        showWorktimerView();
    }

    public void setStageCoordinates() throws Exception {
        WorktimerConfig config = WorktimerConfigHelper.getCurrentConfig();
        primaryStage.setX(config.stageConfig.x);
        primaryStage.setY(config.stageConfig.y);
    }
    
    private void addStageCoordinatesListener() {
        primaryStage.xProperty().addListener((observable, oldValue, newValue) -> saveCurrentStageCoordinates());
        primaryStage.yProperty().addListener((observable, oldValue, newValue) -> saveCurrentStageCoordinates());
    }

    private void showWorktimerView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL worktimerViewUrl = getClass().getResource("view/WorktimerView.fxml");
            loader.setLocation(worktimerViewUrl);
            VBox worktimerView = loader.load();
            primaryStage.setScene(new Scene(worktimerView));

            WorktimerPresenter presenter = loader.getController();
            primaryStage.setOnCloseRequest(new OnCloseHandler(presenter));

            presenter.go(primaryStage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void saveCurrentStageCoordinates() {
        try {
            WorktimerConfig config = WorktimerConfigHelper.getCurrentConfig();
            config.stageConfig.x = primaryStage.getX();
            config.stageConfig.y = primaryStage.getY();
            WorktimerConfigHelper.saveConfig(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // /////////////////////////////////////////////////////////////////
    // Main Method //
    // /////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        launch(args);
    }

}
