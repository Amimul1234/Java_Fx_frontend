package sample.manufacturer;

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
import sample.socket_operation_handeler.Connector;
import sharedClasses.Car_shared;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DeleteCarController {

    private Car_shared car_shared_global = null;
    private List<Car_shared> carSharedList = new ArrayList<>();
    private ObservableList<ModifiedCar> data;

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
    private TextField delete_car_reg_number_for_search;

    @FXML
    private ImageView delete_car_picture;

    @FXML
    private Text delete_car_registration_number_label;

    @FXML
    private Text delete_car_make_label;

    @FXML
    private Text delete_car_model_label;

    @FXML
    private Text delete_car_available_label;

    @FXML
    private Text delete_car_year_made_label;

    @FXML
    private TextField delete_car_registration;

    @FXML
    private TextField delete_car_make;

    @FXML
    private TextField delete_car_model;

    @FXML
    private TextField delete_car_price;

    @FXML
    private Text delete_car_price_label;

    @FXML
    private TextField delete_car_availability;

    @FXML
    private TextField delete_car_year_made;

    @FXML
    private Button delete_button;

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

    public void initialize()
    {
        delete_car_registration_number_label.setVisible(false);
        delete_car_make_label.setVisible(false);
        delete_car_model_label.setVisible(false);
        delete_car_available_label.setVisible(false);
        delete_car_year_made_label.setVisible(false);
        delete_car_registration.setVisible(false);
        delete_car_picture.setVisible(false);
        delete_car_make.setVisible(false);
        delete_car_model.setVisible(false);
        delete_car_price.setVisible(false);
        delete_car_price_label.setVisible(false);
        delete_car_availability.setVisible(false);
        delete_car_year_made.setVisible(false);
        delete_button.setVisible(false);

        initializeColumns();

        populateTable();
    }

    @FXML
    void delete_car_button(ActionEvent event) {

        if(car_shared_global != null)
        {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    writeToServer(car_shared_global);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/role_scene.fxml"));
                            Stage stage = (Stage) delete_button.getScene().getWindow();
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

    @FXML
    void search_for_car(ActionEvent event)
    {

        String reg_number = delete_car_reg_number_for_search.getText();

        if(reg_number.isEmpty())
        {
            showAlert("Error!", "Car registration number can not be empty");
        }
        else
        {
            int i=0;

            for(Car_shared car_shared : carSharedList)
            {
                if(car_shared.getCarReg().equals(reg_number))
                {
                    i=1;
                    delete_car_registration_number_label.setVisible(true);
                    delete_car_make_label.setVisible(true);
                    delete_car_model_label.setVisible(true);
                    delete_car_available_label.setVisible(true);
                    delete_car_year_made_label.setVisible(true);
                    delete_car_registration.setVisible(true);
                    delete_car_picture.setVisible(true);
                    delete_car_make.setVisible(true);
                    delete_car_model.setVisible(true);
                    delete_car_price.setVisible(true);
                    delete_car_price_label.setVisible(true);
                    delete_car_availability.setVisible(true);
                    delete_car_year_made.setVisible(true);
                    delete_button.setVisible(true);

                    delete_car_registration.setText(car_shared.getCarReg());
                    delete_car_make.setText(car_shared.getCarMake());
                    delete_car_model.setText(car_shared.getCarModel());
                    delete_car_price.setText(String.valueOf(car_shared.getPrice()));
                    delete_car_availability.setText(String.valueOf(car_shared.getQuantity()));
                    delete_car_year_made.setText(String.valueOf(car_shared.getYearMade()));
                    delete_car_picture.setImage(new Image(new ByteArrayInputStream(car_shared.getCarImage())));

                    car_shared_global = car_shared;

                    break;
                }
            }

            if(i==0)
            {
                showAlert("Error!", "No car with given registration number exists!");
            }
        }
    }

    private void writeToServer(Car_shared car_shared_global)
    {
        ObjectOutputStream objectOutputStream = Connector.getInstance().getObjectOutputStream();
        try {
            objectOutputStream.writeObject(car_shared_global);
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
