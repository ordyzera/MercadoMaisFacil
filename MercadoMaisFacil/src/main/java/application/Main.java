package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage janela) throws Exception {

        Parent root = FXMLLoader.load(
                getClass().getResource("/application/telainicial.fxml")
        );

        Scene cena = new Scene(root, 1920, 1080);

        cena.getStylesheets().add(
                getClass()
                    .getResource("/application/dashboard.css")
                    .toExternalForm()
        );

        janela.setScene(cena);
        janela.setTitle("Tela Login");
        janela.setMaximized(false);
        janela.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}