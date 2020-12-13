package sample.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.socket_operation_handeler.Connector;
import sharedClasses.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RemoveController {

    private User userGlobal;

    private final List<String> roles = new ArrayList<>()
    {
        {
            add("Admin");
            add("Manufacturer");
            add("Viewer");
        }
    };

    ObservableList<Modified> data;

    private List<User> userList = new ArrayList<>();

    @FXML
    private TableView<Modified> table_of_users;

    @FXML
    private TableColumn<Modified, Integer> user_id;

    @FXML
    private TableColumn<Modified, ImageView> user_image;

    @FXML
    private TableColumn<Modified, String> user_name;

    @FXML
    private TableColumn<Modified, String> user_role;

    @FXML
    private TextField user_id_for_search;

    @FXML
    private ImageView update_image;

    @FXML
    private Text user_name_lable;

    @FXML
    private TextField update_user_name;

    @FXML
    private Text user_password_lable;

    @FXML
    private TextField update_password;

    @FXML
    private Text user_role_lable;

    @FXML
    private ChoiceBox<String> update_role;

    @FXML
    private Button update_user_button;

    private void initializeColumns()
    {
        user_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        user_image.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        user_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        user_role.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void populateTable() //Populating the user table with user data
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    userList = (List<User>) Connector.getInstance().getObjectInputStream().readObject();
                    List<Modified> modifiedList = new ArrayList<>();

                    for(User user : userList)
                    {
                        modifiedList.add(new Modified(user.getName(), user.getImage(), user.getRole(), user.getPassword(), user.getActions(), user.getUser_id()));
                    }

                    data = FXCollections.observableList(modifiedList);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            table_of_users.setItems(data);
                        }
                    });

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void initialize()
    {
        update_user_button.setVisible(false);
        update_role.setVisible(false);
        user_role_lable.setVisible(false);
        update_password.setVisible(false);
        user_password_lable.setVisible(false);
        update_user_name.setVisible(false);
        user_name_lable.setVisible(false);
        update_image.setVisible(false);

        update_role.getItems().addAll(roles);

        initializeColumns();

        populateTable();
    }

    @FXML
    void search_for_user(ActionEvent event) {
        String user_id = user_id_for_search.getText();

        if(user_id.isEmpty())
        {
            showAlert("Error!", "User id can not be empty");
        }

        try
        {
            int id = Integer.parseInt(user_id);

            for(User user : userList)
            {
                if(user.getUser_id() == id)
                {
                    update_user_button.setVisible(true);
                    update_role.setVisible(true);
                    user_role_lable.setVisible(true);
                    update_password.setVisible(true);
                    user_password_lable.setVisible(true);
                    update_user_name.setVisible(true);
                    user_name_lable.setVisible(true);
                    update_image.setVisible(true);

                    update_user_name.setText(user.getName());
                    update_password.setText(user.getPassword());
                    update_image.setImage(new Image(new ByteArrayInputStream(user.getImage())));

                    for(int i=0; i<3; i++)
                    {
                        if(roles.get(i).equals(user.getRole()))
                        {
                            update_role.getSelectionModel().select(i);
                            break;
                        }
                    }
                    userGlobal = user;
                    break;
                }
            }

        }catch (Exception e)
        {
            showAlert("ERROR", e.getLocalizedMessage());
        }
    }

    @FXML
    void delete_user(ActionEvent event)
    {
        if(userGlobal != null)
        {
            userGlobal.setName(update_user_name.getText());
            userGlobal.setPassword(update_password.getText());
            userGlobal.setRole(update_role.getSelectionModel().getSelectedItem());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    writeToServer(userGlobal);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/role_scene.fxml"));
                            Stage stage = (Stage) update_user_button.getScene().getWindow();
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
            }).start();

        }

        else {
            showAlert("Error!", "Please select an user to continue");
        }
    }

    private void writeToServer(User userGlobal)
    {
        ObjectOutputStream objectOutputStream = Connector.getInstance().getObjectOutputStream();
        try {
            objectOutputStream.writeObject(userGlobal);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void showAlert(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
