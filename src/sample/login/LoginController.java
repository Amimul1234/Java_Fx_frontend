package sample.login;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Prevalent;
import sample.socket_operation_handeler.Connector;
import sample.socket_operation_handeler.Connector_2_for_user_list_update;
import sample.socket_operation_handeler.Connector_3_for_car_list_update;
import sharedClasses.LoginReq;
import sharedClasses.User;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class LoginController {

    private final Connector connector = Connector.getInstance();
    private final Connector_2_for_user_list_update connector_2_for_user_list_update = Connector_2_for_user_list_update.getInstance();
    private final Connector_3_for_car_list_update connector_3_for_car_list_update = Connector_3_for_car_list_update.getInstance();

    @FXML
    private TextField userName;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField password;

    @FXML
    void login(ActionEvent event) throws IOException {

        String user_name = userName.getText();
        String pass = password.getText();

        if(user_name.isEmpty())
        {
            showAlert(Alert.AlertType.ERROR,"Error!", "Please enter username");
        }

        else
        {
            LoginReq loginReq = new LoginReq();
            loginReq.setUserName(user_name);
            loginReq.setPassword(pass);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    writeToServer(loginReq);
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    checkUser();
                }
            }).start();
        }
    }

    private void writeToServer(LoginReq loginReq)//Write user name and password for checking
    {

        ObjectOutputStream objectOutputStream = Connector.getInstance().getObjectOutputStream();

        try {
            objectOutputStream.writeObject(loginReq);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkUser() //checking user authentication
    {

        User user = null;

        try {
            user = (User) connector.getObjectInputStream().readObject();

            if(user.isSuccessful())
            {
                Prevalent.setName(user.getName());
                Prevalent.setImage(user.getImage());
                Prevalent.setRole(user.getRole());
                Prevalent.setActions(user.getActions());

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/role_scene.fxml"));
                        Stage stage = (Stage) loginButton.getScene().getWindow();
                        Scene scene = null;
                        try {
                            scene = new Scene(loader.load(), 1114, 627);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stage.setScene(scene);
                    }
                });
            }

            else
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(Alert.AlertType.ERROR,"Credential Error!", "Please enter correct username and password");
                    }
                });
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
