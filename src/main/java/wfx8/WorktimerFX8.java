package wfx8;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wfx8.presenter.WorktimerPresenter;
import wfx8.util.PrimaryStageHelper;
import wfx8.util.ReadWriteException;

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
            URL worktimerViewUrl = getClass().getResource("view/WorktimerView.fxml"); 
            loader.setLocation(worktimerViewUrl);
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
