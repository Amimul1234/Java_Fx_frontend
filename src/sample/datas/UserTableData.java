package sample.datas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.admin.ModifiedUser;
import sample.login.Main;
import sharedClasses.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserTableData {

    private List<User> userList;
    private List<ModifiedUser> modifiedUserList;
    private ObservableList<ModifiedUser> data;

    private static UserTableData userTableData = null;

    private UserTableData() {
        this.userList = new ArrayList<>();
        this.modifiedUserList = new ArrayList<>();
        this.data = FXCollections.observableArrayList();

        populate();
    }

    public static UserTableData getInstance()
    {
        if(userTableData == null)
            userTableData = new UserTableData();

        return userTableData;
    }

    private void populate() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    try {
                        userList.clear();
                        userList.addAll((List<User>) Main.connector_2_for_user_list_update.getObjectInputStream().readObject());
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }

                    modifiedUserList.clear();

                    for(User user : userList)
                    {
                        modifiedUserList.add(new ModifiedUser(user.getName(), user.getImage(), user.getRole(),
                                user.getPassword(), user.getActions(), user.getUser_id()));
                    }
                    data.clear();
                    data.addAll(modifiedUserList);
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public ObservableList<ModifiedUser> getData() {
        return data;
    }
}
