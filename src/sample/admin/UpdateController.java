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
import sample.socket.Connector;
import sharedClasses.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateController {

    private User userGlobal;
    private Thread thread;
    private int thread_runner = 1;

    private final List<String> roles = new ArrayList<>()
    {
        {
            add("Admin");
            add("Manufacturer");
            add("Viewer");
        }
    };

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

    ObservableList<Modified> data;
    private List<User> userList = new ArrayList<>();

    private void initializeColumns()
    {
        user_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        user_image.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        user_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        user_role.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void populateTable()
    {
        try {

            userList = (List<User>) Connector.getInstance().getObjectInputStream().readObject();

            List<Modified> modifiedList = new ArrayList<>();

            for(User user : userList)
            {
                modifiedList.add(new Modified(user.getName(), user.getImage(), user.getRole(), user.getPassword(), user.getActions(), user.getUser_id()));
            }

            data = FXCollections.observableList(modifiedList);

            table_of_users.setItems(data);


            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true)
                    {
                        try {
                            if(thread_runner == 1)
                            {
                                System.out.println("Calling thread");

                                userList = (List<User>) Connector.getInstance().getObjectInputStream().readObject();

                                modifiedList.clear();

                                for(User user : userList)
                                {
                                    modifiedList.add(new Modified(user.getName(), user.getImage(), user.getRole(), user.getPassword(), user.getActions(), user.getUser_id()));
                                }

                                table_of_users.refresh();
                            }
                            else
                            {
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            });

            thread.start();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initialize()
    {
        System.out.println("Called the update");
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

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                populateTable();
            }
        });
    }


    @FXML
    void search_for_user(ActionEvent event) {
        String user_id = user_id_for_search.getText();

        if(user_id.isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Error!", "User id can not be empty");
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
            showAlert(Alert.AlertType.ERROR, "ERROR", e.getLocalizedMessage());
        }
    }

    @FXML
    void update_user(ActionEvent event) {
        if(userGlobal != null)
        {
            userGlobal.setName(update_user_name.getText());
            userGlobal.setPassword(update_password.getText());
            userGlobal.setRole(update_role.getSelectionModel().getSelectedItem());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        ObjectOutputStream objectOutputStream = Connector.getInstance().getObjectOutputStream();
                        objectOutputStream.writeObject(userGlobal);
                        objectOutputStream.flush();

                        thread_runner = 0;

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/role_scene.fxml"));
                        Stage stage = (Stage) update_user_button.getScene().getWindow();
                        Scene scene = new Scene(loader.load(), 1114, 627);
                        stage.setScene(scene);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        else {
            showAlert(Alert.AlertType.ERROR, "Error!", "Please select an user to continue");
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
