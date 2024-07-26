package sample.krankenhaussystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class KrankenhaussystemApp extends Application {

    MainController mc;
    Patient p ;
    @Override
    public void start(Stage stage) throws IOException {
        //Ein neue patient erstellen.
        p=new Patient();
        //main-view lesen und in scene laden, und dann scene zu stage hinzufügen.
        FXMLLoader fxmlLoader = new FXMLLoader(KrankenhaussystemApp.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1300, 800);
        stage.setTitle("Krankenhaussystem");
        stage.setScene(scene);
        stage.show();
        //Aktullen Controller in mc speicheren.
        mc=fxmlLoader.getController();
        //Der patient zum dem aktuellen Controller hinzufügen.
        mc.setPatient(p);
    }

    public static void main(String[] args) {
        launch();
    }
}
