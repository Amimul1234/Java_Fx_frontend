package sample.viewer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import sample.datas.CarTableData;
import sample.login.Main;
import sample.manufacturer.ModifiedCar;
import sample.socket_operation_handeler.Connector;
import sharedClasses.Car_shared;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BuyCarController {

    private Car_shared car_shared_global = null;
    private List<Car_shared> carSharedList = new ArrayList<>();
    private ObservableList<ModifiedCar> data;
    private int clicked_state = 0;

    @FXML
    private TableView<ModifiedCar> table_of_cars;

    @FXML
    private TableColumn<ModifiedCar, ImageView> car_image;

    @FXML
    private TableColumn<ModifiedCar, String> registration_number;

    @FXML
    private TableColumn<ModifiedCar, String> car_model;

    @FXML
    private TableColumn<ModifiedCar, Integer> car_price;

    @FXML
    private TableColumn<ModifiedCar, Integer> available_quantity;

    @FXML
    private Text delete_car_search_label;

    @FXML
    private TextField update_car_registration_number_for_search;

    @FXML
    private ImageView update_car_picture;

    @FXML
    private Text update_car_registration_label;

    @FXML
    private TextField update_car_registration;

    @FXML
    private Text update_car_make_label;

    @FXML
    private TextField update_car_make;

    @FXML
    private Text update_car_model_label;

    @FXML
    private Button buy_car_button;

    @FXML
    private TextField update_car_model;

    @FXML
    private TextField update_car_price;

    @FXML
    private Text update_car_price_label;

    @FXML
    private TextField buying_quantity;

    @FXML
    private TextField update_car_year_made;

    @FXML
    private Text update_car_available_label;

    @FXML
    private Text update_car_year_made_label;

    @FXML
    private Text update_car_colour1_label;

    @FXML
    private TextField update_car_colour1;

    @FXML
    private Text update_car_colour2_label;

    @FXML
    private TextField update_car_colour2;

    @FXML
    private Text update_car_colour3_label;

    @FXML
    private TextField update_car_colour3;

    private void initializeColumns()
    {
        car_image.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        registration_number.setCellValueFactory(new PropertyValueFactory<>("CarReg"));
        car_model.setCellValueFactory(new PropertyValueFactory<>("CarModel"));
        car_price.setCellValueFactory(new PropertyValueFactory<>("Price"));
        available_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void populateTable() //Populating the user table with user data
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    carSharedList = (List<Car_shared>) Main.connector.getObjectInputStream().readObject();

                    List<ModifiedCar> modifiedCarList = new ArrayList<>();

                    for(Car_shared car_shared : carSharedList)
                    {
                        modifiedCarList.add(new ModifiedCar(car_shared.getQuantity(), car_shared.getCarReg(), car_shared.getYearMade(),
                                car_shared.getColour1(), car_shared.getColour2(), car_shared.getColour3(), car_shared.getCarMake(),
                                car_shared.getCarModel(), car_shared.getPrice(), car_shared.getCarImage()));
                    }

                    data = FXCollections.observableList(modifiedCarList );

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            table_of_cars.setItems(data);
                            table_of_cars.refresh();
                        }
                    });

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        CarTableData carTableData = CarTableData.getInstance();

        carTableData.getData().addListener(new ListChangeListener<ModifiedCar>() {
            @Override
            public void onChanged(Change<? extends ModifiedCar> change) {
                data.clear();
                data.addAll(carTableData.getData());
                table_of_cars.refresh();
            }
        });
    }

    @FXML
    void buy_car(ActionEvent event) {

        int buying_quantity_ = Integer.parseInt(buying_quantity.getText());

        if(buying_quantity_ < 0)
        {
            showAlert("Buying quantity can not be negative");
            return;
        }

        for(ModifiedCar modifiedCar : data)
        {
            if(modifiedCar.getCarReg().equals(car_shared_global.getCarReg()))
            {
                if(modifiedCar.getQuantity() < buying_quantity_)
                {
                    car_shared_global.setQuantity(modifiedCar.getQuantity());
                    showAlert("Can not buy more than available cars");
                    return;
                }
            }
        }

        if(car_shared_global != null)
        {
            car_shared_global.setCarReg(update_car_registration.getText());
            car_shared_global.setYearMade(Integer.parseInt(update_car_year_made.getText()));
            car_shared_global.setColour1(update_car_colour1.getText());
            car_shared_global.setColour2(update_car_colour2.getText());
            car_shared_global.setColour3(update_car_colour3.getText());
            car_shared_global.setCarMake(update_car_make.getText());
            car_shared_global.setCarModel(update_car_model.getText());
            car_shared_global.setPrice(Integer.parseInt(update_car_price.getText()));
            car_shared_global.setQuantity(car_shared_global.getQuantity() - Integer.parseInt(buying_quantity.getText()));

            new Thread(new Runnable() {
                @Override
                public void run() {

                    writeToSever(car_shared_global);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/role_scene.fxml"));
                            Stage stage = (Stage) buy_car_button.getScene().getWindow();
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
    }

    @FXML
    void search_for_car(ActionEvent event) {

        String reg_number = update_car_registration_number_for_search.getText();

        if(reg_number.isEmpty())
        {
            showAlert("Car registration number can not be empty");
        }

        else
        {
            int i=0;

            for(Car_shared car_shared : carSharedList)
            {
                if(car_shared.getCarReg().equals(reg_number))
                {
                    i=1;

                    visibility_changer(true);

                    update_car_registration.setText(car_shared.getCarReg());
                    update_car_year_made.setText(String.valueOf(car_shared.getYearMade()));
                    update_car_colour1.setText(car_shared.getColour1());
                    update_car_colour2.setText(car_shared.getColour2());
                    update_car_colour3.setText(car_shared.getColour3());
                    update_car_make.setText(car_shared.getCarMake());
                    update_car_model.setText(car_shared.getCarModel());
                    update_car_price.setText(String.valueOf(car_shared.getPrice()));
                    buying_quantity.setText(String.valueOf(car_shared.getQuantity()));
                    update_car_picture.setImage(new Image(new ByteArrayInputStream(car_shared.getCarImage())));

                    car_shared_global = car_shared;

                    clicked_state = 1;

                    break;
                }
            }
            if(i==0)
            {
                showAlert("No car with given registration number exists!");
            }
        }
    }


    public void initialize()
    {
        visibility_changer(false);
        initializeColumns();
        populateTable();
    }

    private void writeToSever(Car_shared car_shared)
    {
        ObjectOutputStream objectOutputStream = Connector.getInstance().getObjectOutputStream();
        try {
            objectOutputStream.writeObject(car_shared);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void visibility_changer(boolean b)
    {
        update_car_picture.setVisible(b);
        update_car_registration_label.setVisible(b);
        update_car_registration.setVisible(b);
        update_car_make_label.setVisible(b);
        update_car_make.setVisible(b);
        update_car_model_label.setVisible(b);
        buy_car_button.setVisible(b);
        update_car_model.setVisible(b);
        update_car_price.setVisible(b);
        update_car_price_label.setVisible(b);
        buying_quantity.setVisible(b);
        update_car_year_made.setVisible(b);
        update_car_available_label.setVisible(b);
        update_car_year_made_label.setVisible(b);
        update_car_colour1_label.setVisible(b);
        update_car_colour1.setVisible(b);
        update_car_colour2_label.setVisible(b);
        update_car_colour2.setVisible(b);
        update_car_colour3_label.setVisible(b);
        update_car_colour3.setVisible(b);
    }

    private static void showAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
