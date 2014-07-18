package wfx8;

import wfx8.presenter.WorktimerPresenter;
import wfx8.util.PrimaryStageHelper;
import wfx8.util.ReadWriteException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WorktimerFX8 extends Application {
    
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Worktimer FX 8");
        PrimaryStageHelper.setStageCoordinates(primaryStage);
        
        showWorktimerView();
    }
    
    private void showWorktimerView() throws ReadWriteException {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(WorktimerFX8.class.getResource("view/WorktimerView.fxml"));
            VBox worktimerView = loader.load();
            primaryStage.setScene(new Scene(worktimerView));
            
            WorktimerPresenter presenter = loader.getController();
            primaryStage.setOnCloseRequest(new OnCloseHandler(presenter, primaryStage));
            
            
            presenter.go(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    ///////////////////////////////////////////////////////////////////
    // Main Method                                                   //
    ///////////////////////////////////////////////////////////////////
    
    public static void main(String[] args) {
        launch(args);
    }

}
