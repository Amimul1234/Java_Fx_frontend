package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.socket.Connector;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RoleController {
    @FXML
    private ImageView user_profile_pic;

    @FXML
    private Text user_name;

    @FXML
    private ListView<String> avilable_options;

    @FXML
    private TextField option_input;

    @FXML
    private Button select_choice;

    @FXML
    private Text name_of_the_user;

    @FXML
    private Text role_of_the_user;

    @FXML
    public void initialize() {
        user_name.setText(Prevalent.getUser_name());
        name_of_the_user.setText(Prevalent.getUser_name());
        role_of_the_user.setText(Prevalent.getRole());

        for(int i=0; i<Prevalent.getOptions().size(); i++)
        {
            avilable_options.getItems().add(String.valueOf(i+1)+". "+Prevalent.getOptions().get(i));
        }
    }

    @FXML
    void action_taker(ActionEvent event) {
        String option = option_input.getText();

        if(option.isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Field error", "Please enter your choice");
            return;
        }
        try
        {
            int value = Integer.parseInt(option_input.getText());

            if(value > Prevalent.getOptions().size())
            {
                showAlert(Alert.AlertType.ERROR, "Input Error !", "Please enter correct option");
            }
            else
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        sendNecessaryActionToServer();
                    }
                });
            }

        }catch (Exception e)
        {
            showAlert(Alert.AlertType.ERROR, "Input Error !", "Please enter correct option");
        }
    }

    private void sendNecessaryActionToServer() {
        try
        {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(Connector.getInstance().getSocket().getOutputStream());
            List<String> clientResponse = new ArrayList<>();
            clientResponse.add(option_input.getText());
            objectOutputStream.writeObject(clientResponse);
            objectOutputStream.flush();

            //This block was for all user option selector
            //From next block gonna add role specific actions

            int value = Integer.parseInt(option_input.getText());

            if(Prevalent.getRole().equals("Admin"))
            {
                if(value == 1)
                {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/admin/create.fxml"));
                    Stage stage = (Stage) select_choice.getScene().getWindow();
                    stage.setTitle("Create a New User");
                    Scene scene = new Scene(loader.load(), 1114, 627);
                    stage.setScene(scene);
                }
                else if(value == 2)
                {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/admin/update.fxml"));
                    Stage stage = (Stage) select_choice.getScene().getWindow();
                    stage.setTitle("Update User Info");
                    Scene scene = new Scene(loader.load(), 1114, 627);
                    stage.setScene(scene);
                }
                else if(value == 3)
                {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/admin/delete.fxml"));
                    Stage stage = (Stage) select_choice.getScene().getWindow();
                    stage.setTitle("Delete an User");
                    Scene scene = new Scene(loader.load(), 1114, 627);
                    stage.setScene(scene);
                }
            }
            else if(Prevalent.getRole().equals("Viewer"))
            {

            }
            else if(Prevalent.getRole().equals("Manufacturer"))
            {

            }
        }catch (Exception e)
        {
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