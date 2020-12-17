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
import sample.login.Main;
import sample.socket_operation_handeler.Connector;
import sharedClasses.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateController {

    private User userGlobal;
    public static Thread prev_thread = null;
    public static List<User> userList = new ArrayList<>();

    private final List<String> roles = new ArrayList<>()
    {
        {
            add("Admin");
            add("Manufacturer");
            add("Viewer");
        }
    };

    ObservableList<ModifiedUser> data;


    @FXML
    private TableView<ModifiedUser> table_of_users;

    @FXML
    private TableColumn<ModifiedUser, Integer> user_id;

    @FXML
    private TableColumn<ModifiedUser, ImageView> user_image;

    @FXML
    private TableColumn<ModifiedUser, String> user_name;

    @FXML
    private TableColumn<ModifiedUser, String> user_role;

    @FXML
    private TextField user_id_for_search;

    @FXML
    private ImageView update_image;

    @FXML
    private Text user_name_label;

    @FXML
    private TextField update_user_name;

    @FXML
    private Text user_password_label;

    @FXML
    private TextField update_password;

    @FXML
    private Text user_role_label;

    @FXML
    private ChoiceBox<String> update_role;

    @FXML
    private Button update_user_button;

    private void initializeColumns() //Getting table data from user class
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
                    userList = (List<User>) Main.connector.getObjectInputStream().readObject();

                    List<ModifiedUser> modifiedUserList = new ArrayList<>();

                    for(User user : userList)
                    {
                        modifiedUserList.add(new ModifiedUser(user.getName(), user.getImage(), user.getRole(), user.getPassword(), user.getActions(), user.getUser_id()));
                    }

                    data = FXCollections.observableList(modifiedUserList);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            table_of_users.setItems(data);
                            table_of_users.refresh();
                        }
                    });

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        if(prev_thread == null)
        {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true)
                    {
                        try {
                            userList = (List<User>) Main.connector_2_for_user_list_update.getObjectInputStream().readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        List<ModifiedUser> modifiedUserList = new ArrayList<>();

                        for(User user : userList)
                        {
                            modifiedUserList.add(new ModifiedUser(user.getName(), user.getImage(), user.getRole(),
                                    user.getPassword(), user.getActions(), user.getUser_id()));
                        }

                        data = FXCollections.observableList(modifiedUserList);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                table_of_users.setItems(data);
                                table_of_users.refresh();
                            }
                        });

                    }
                }
            });
            prev_thread = thread;
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void initialize()
    {
        update_user_button.setVisible(false);
        update_role.setVisible(false);
        user_role_label.setVisible(false);
        update_password.setVisible(false);
        user_password_label.setVisible(false);
        update_user_name.setVisible(false);
        user_name_label.setVisible(false);
        update_image.setVisible(false);

        update_role.getItems().addAll(roles);

        initializeColumns();

        populateTable();
    }

    @FXML
    void search_for_user(ActionEvent event)
    {

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
                    user_role_label.setVisible(true);
                    update_password.setVisible(true);
                    user_password_label.setVisible(true);
                    update_user_name.setVisible(true);
                    user_name_label.setVisible(true);
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
    void update_user(ActionEvent event)
    {
        if(userGlobal != null)
        {

            userGlobal.setName(update_user_name.getText());
            userGlobal.setPassword(update_password.getText());
            userGlobal.setRole(update_role.getSelectionModel().getSelectedItem());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    writeToSever(userGlobal);

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

    private void writeToSever(User userGlobal)
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
