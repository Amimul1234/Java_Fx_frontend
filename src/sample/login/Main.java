package sample.login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.socket_operation_handeler.Connector;
import sample.socket_operation_handeler.Connector_2_for_user_list_update;
import sample.socket_operation_handeler.Connector_3_for_car_list_update;

public class Main extends Application {

    public static Connector_2_for_user_list_update connector_2_for_user_list_update = Connector_2_for_user_list_update.getInstance();
    public static Connector_3_for_car_list_update connector_3_for_car_list_update = Connector_3_for_car_list_update.getInstance();
    public static Connector connector = Connector.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 1114, 627));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
