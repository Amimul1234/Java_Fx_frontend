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
import java.util.List;

public class SearchByMakeModelController {

    @FXML
    private TextField car_make;

    @FXML
    private TextField car_model;

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
                            Stage stage = (Stage) car_make.getScene().getWindow();
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

    @FXML
    void search_for_car(ActionEvent event) {
        if(car_make.getText().isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Error!", "Car registration number can not be empty");
        }
        else if(car_model.getText().isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "ERROR!", "Car model can not be empty");
        }
        else
        {
            String car_make_model = car_make.getText() + "____" + car_model.getText();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    ObjectOutputStream objectOutputStream = Main.connector.getObjectOutputStream();

                    try {
                        objectOutputStream.writeObject(car_make_model);
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
                            List<Car_shared> car_sharedList = (List<Car_shared>) object;

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Parent root;

                                    try {
                                        FXMLLoader fxmlLoader = new FXMLLoader();
                                        fxmlLoader.setLocation(getClass().getResource("/sample/viewer/car_details_list_for_search.fxml"));
                                        Scene scene = new Scene(fxmlLoader.load(), 1114, 627);
                                        Stage stage = new Stage();
                                        stage.setTitle("Car Details");
                                        stage.initStyle(StageStyle.DECORATED);
                                        stage.setScene(scene);

                                        CarDetailsListForSearch carDetailsListForSearch = fxmlLoader.getController();
                                        carDetailsListForSearch.setCars(car_sharedList);

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
            }).start();
        }
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
