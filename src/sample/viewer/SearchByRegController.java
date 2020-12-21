package sample.viewer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.login.Main;
import sharedClasses.Car_shared;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SearchByRegController {

    @FXML
    private TextField registration_number;

    @FXML
    void search_car_by_reg(ActionEvent event) {

        if(registration_number.getText().isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "ERROR!", "Registration number can not be empty");
        }

        else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    ObjectOutputStream objectOutputStream = Main.connector.getObjectOutputStream();

                    try {
                        objectOutputStream.writeObject(registration_number.getText());
                        objectOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Object object = Main.connector.getObjectInputStream().readObject();

                        if(object instanceof String)
                        {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    showAlert(Alert.AlertType.ERROR, "ERROR!", (String) object);
                                }
                            });

                        }
                        else
                        {
                            Car_shared car_shared = (Car_shared) object;

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Parent root;

                                    try {
                                        FXMLLoader fxmlLoader = new FXMLLoader();
                                        fxmlLoader.setLocation(getClass().getResource("/sample/viewer/car_details.fxml"));
                                        Scene scene = new Scene(fxmlLoader.load(), 900, 550);
                                        Stage stage = new Stage();
                                        stage.setTitle("Car Details");
                                        stage.initStyle(StageStyle.DECORATED);
                                        stage.setScene(scene);

                                        Car_details car_details = fxmlLoader.getController();
                                        car_details.setCar(car_shared);

                                        stage.show();

                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }
    }

    @FXML
    void back_to_main_menu(ActionEvent event) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectOutputStream objectOutputStream = Main.connector.getObjectOutputStream();

                try {
                    objectOutputStream.writeObject("Back to main menu");
                    objectOutputStream.flush();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/role_scene.fxml"));
                            Stage stage = (Stage) registration_number.getScene().getWindow();
                            Scene scene = null;
                            try {
                                scene = new Scene(loader.load(), 1114, 627);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            stage.setScene(scene);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message)
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
