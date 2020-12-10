package sample.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import sample.socket.Connector;
import sharedClasses.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class UpdateController {

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

    ObservableList<Modified> data;
    private List<User> userList = new ArrayList<>();

    private void initializeColumns() {
        user_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        user_image.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        user_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        user_role.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    public void initialize() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initializeColumns();
                populateTable();
            }
        });
    }

    private void populateTable() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(Connector.getInstance().getSocket().getInputStream());
            userList = (List<User>) objectInputStream.readObject();
            List<Modified> modifiedList = new ArrayList<>();

            for(User user : userList)
            {
                modifiedList.add(new Modified(user.getName(), user.getImage(), user.getRole(), user.getPassword(), user.getActions(), user.getUser_id()));
            }

            data = FXCollections.observableArrayList(modifiedList);

            table_of_users.setItems(data);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
