package wfx8;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wfx8.model.WorktimerConfig;
import wfx8.presenter.WorktimerPresenter;
import wfx8.util.WorktimerConfigHelper;

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
        primaryStage.setX(config.stageX);
        primaryStage.setY(config.stageY);
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
            primaryStage.setOnCloseRequest(new OnCloseHandler(presenter, primaryStage));

            presenter.go(primaryStage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void saveCurrentStageCoordinates() {
        try {
            WorktimerConfig config = WorktimerConfigHelper.getCurrentConfig();
            config.stageX = primaryStage.getX();
            config.stageY = primaryStage.getY();
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
