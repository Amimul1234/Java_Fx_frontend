package sample.manufacturer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.socket_operation_handeler.Connector;
import sharedClasses.Car_shared;
import java.io.*;

public class AddCarController {

    private File file;

    @FXML
    private ImageView car_photo_upload;

    @FXML
    private TextField car_registration_number;

    @FXML
    private TextField car_year_made;

    @FXML
    private TextField car_colour_1;

    @FXML
    private TextField car_colour_2;

    @FXML
    private TextField car_colour_3;

    @FXML
    private TextField car_make;

    @FXML
    private TextField car_model;

    @FXML
    private TextField car_price;

    @FXML
    private TextField car_quantity;

    @FXML
    void add_car_to_showroom(ActionEvent event) {

        if(car_registration_number.getText().isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Error!", "Car registration number can not be empty!");
        }
        else if(car_year_made.getText().isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Error!", "Car year made can not be empty!");
        }
        else if(car_make.getText().isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Error!", "Car make can not be empty!");
        }
        else if(car_model.getText().isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Error!", "Car model can not be empty!");
        }
        else if(car_price.getText().isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Error!", "Car price can not be empty!");
        }
        else if(car_quantity.getText().isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Error!", "Car quantity can not be empty!");
        }
        else if(file == null)
        {
            showAlert(Alert.AlertType.ERROR, "Error!", "Please select a car image!");
        }

        else{
            Car_shared car_shared = new Car_shared();

            car_shared.setCarReg(car_registration_number.getText());
            car_shared.setCarMake(car_make.getText());
            car_shared.setYearMade(Integer.parseInt(car_year_made.getText()));
            car_shared.setCarModel(car_model.getText());
            car_shared.setPrice(Integer.parseInt(car_price.getText()));
            car_shared.setQuantity(Integer.parseInt(car_quantity.getText()));
            car_shared.setColour1(car_colour_1.getText());
            car_shared.setColour2(car_colour_2.getText());
            car_shared.setColour3(car_colour_3.getText());

            try {
                FileInputStream fileInputStream = new FileInputStream(file.getPath());
                car_shared.setByteArraySize((int) file.length());
                fileInputStream.read(car_shared.getCarImage(), 0, car_shared.getCarImage().length);
                fileInputStream.close();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeToServer(car_shared);
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String response = readServerResponse();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if(response.equals("success"))
                                {
                                    showAlert(Alert.AlertType.INFORMATION, "Success", "Car added");
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/role_scene.fxml"));
                                    Stage stage = (Stage) car_colour_1.getScene().getWindow();
                                    Scene scene = null;

                                    try {
                                        scene = new Scene(loader.load(), 1114, 627);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    stage.setScene(scene);
                                }
                                else
                                {
                                    showAlert(Alert.AlertType.ERROR, "Error!", "Can not add car, check server!!");
                                }
                            }
                        });
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error!", "Error occurred while reading from file");
                return;
            }
        }
    }

    @FXML
    void open_file_chooser(MouseEvent event)
    {

        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg"); //getting only images

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(imageFilter);
        chooser.setTitle("Select Image");

        file = chooser.showOpenDialog(car_colour_1.getScene().getWindow());

        if(file != null)
        {
            Image image = new Image(file.toURI().toString());
            car_photo_upload.setFitWidth(400);
            car_photo_upload.setFitHeight(240);
            car_photo_upload.setImage(image);
            car_photo_upload.setSmooth(true);
            car_photo_upload.setCache(true);
        }
    }

    private String readServerResponse() {
        ObjectInputStream objectInputStream = Connector.getInstance().getObjectInputStream();
        try {
            return (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Error!";
        }
    }

    private void writeToServer(Car_shared car_shared) {
        ObjectOutputStream objectOutputStream = Connector.getInstance().getObjectOutputStream();
        try {
            objectOutputStream.writeObject(car_shared);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
