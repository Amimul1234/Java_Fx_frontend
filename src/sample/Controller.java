package sample;

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
import sample.socket.Connector;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final Connector connector = Connector.getInstance();

    @FXML
    private TextField userName;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField password;

    @FXML
    void login(ActionEvent event) throws IOException {

        List<String> credential = new ArrayList<>();

        String user_name = userName.getText();
        String pass = password.getText();

        if(user_name.isEmpty())
        {
            showAlert(Alert.AlertType.ERROR,"Form Error!", "Please enter username");
            return;
        }

        else if(pass.isEmpty())
        {
            showAlert(Alert.AlertType.ERROR,"Form error!", "Please enter password");
            return;
        }
        else
        {
            credential.add(user_name);
            credential.add(pass);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    checkUser(credential);
                }
            });

        }
    }

    private void checkUser(List<String> credential)
    {
        try {

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(connector.getSocket().getOutputStream());

            objectOutputStream.writeObject(credential);
            objectOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(connector.getSocket().getInputStream());
            List<String> response = (List<String>) objectInputStream.readObject();

            if(response.get(0).equals("false"))
            {
                Prevalent.setUser_name(response.get(1));
                Prevalent.setImage_url(response.get(2));
                Prevalent.setRole(response.get(3));

                int size = response.size();

                for(int i=4; i<size; i++)
                {
                    Prevalent.getOptions().add(response.get(i));
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("role_scene.fxml"));
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
