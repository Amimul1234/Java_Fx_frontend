package sample.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.socket.Connector;
import sharedClasses.User;

import java.io.*;
import java.util.Arrays;

public class CreateController {

    private File file;

    @FXML
    private ImageView photo_upload_button;

    @FXML
    private TextField username;

    @FXML
    private PasswordField user_password;

    @FXML
    private ChoiceBox<String> role_drop_down;

    @FXML
    private Button create_user_button;

    @FXML
    private void initialize()
    {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll("Admin", "Viewer","Manufacturer");
        role_drop_down.setItems(list);
        role_drop_down.getSelectionModel().selectFirst();
    }

    @FXML
    void create_a_new_user(ActionEvent event) {
        String user_name = username.getText();

        if(user_name.isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Error!", "User Name Cannot be empty");
        }
        else if(file == null)
        {
            showAlert(Alert.AlertType.WARNING, "Image", "Please select user image");
        }
        else{

            User user = new User();
            user.setName(user_name);
            user_password.setText(user_password.getText());
            user.setRole(role_drop_down.getValue());

            try {
                FileInputStream fileInputStream = new FileInputStream(file.getPath());
                user.setByteArraySize((int) file.length());
                fileInputStream.read(user.getImage(), 0, user.getImage().length);
                fileInputStream.close();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(Connector.getInstance().getSocket().getOutputStream());
                            objectOutputStream.writeObject(user);
                            objectOutputStream.flush();

                            ObjectInputStream objectInputStream = new ObjectInputStream(Connector.getInstance().getSocket().getInputStream());
                            String response = (String) objectInputStream.readObject();

                            if(response.equals("success"))
                            {
                                showAlert(Alert.AlertType.INFORMATION, "Success", "User created");
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/role_scene.fxml"));
                                Stage stage = (Stage) create_user_button.getScene().getWindow();
                                Scene scene = new Scene(loader.load(), 1114, 627);
                                stage.setScene(scene);
                            }

                            else
                            {
                                showAlert(Alert.AlertType.ERROR, "Errow!", "Can not create user");
                            }

                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Error!", e.getLocalizedMessage());
                        }
                    }
                });

                //Image Retrieval code
                /*
                System.out.println(Arrays.toString(user.getImage()));
                FileOutputStream fileOutputStream = new FileOutputStream("G://abcd.jpg");
                fileOutputStream.write(user.getImage(), 0, user.getImage().length);
                */


            } catch (Exception e) {
                e.printStackTrace();
                 showAlert(Alert.AlertType.ERROR, "Error!", "Error occurred while reading from file");
                 return;
            }
        }
    }

    @FXML
    void open_file_chooser(MouseEvent event) {
        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg"); //getting only images

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(imageFilter);
        chooser.setTitle("Select Image");

        file = chooser.showOpenDialog(create_user_button.getScene().getWindow());

        if(file != null)
        {
            Image image = new Image(file.toURI().toString());
            photo_upload_button.setFitWidth(400);
            photo_upload_button.setFitHeight(240);
            photo_upload_button.setImage(image);
            photo_upload_button.setPreserveRatio(true);
            photo_upload_button.setSmooth(true);
            photo_upload_button.setCache(true);
        }
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
