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
import sample.socket.Connector;
import sharedClasses.LoginReq;
import sharedClasses.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LoginController {

    private final Connector connector = Connector.getInstance();

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

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    checkUser(loginReq);//Running check on a different thread
                }
            });

        }
    }

    private void checkUser(LoginReq loginReq) //checking user authentication
    {
        try {

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(connector.getSocket().getOutputStream());

            objectOutputStream.writeObject(loginReq);
            objectOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(connector.getSocket().getInputStream());

            User user = (User) objectInputStream.readObject();


            if(user.isSuccessful())
            {
                Prevalent.setName(user.getName());
                Prevalent.setImage(user.getImage());
                Prevalent.setRole(user.getRole());
                Prevalent.setActions(user.getActions());

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/role_scene.fxml"));
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(loader.load(), 1114, 627);
                stage.setScene(scene);
            }
            else
            {
                showAlert(Alert.AlertType.ERROR,"Credential Error!", "Please enter correct username and password");
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
