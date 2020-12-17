package sample.manufacturer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import sample.login.Main;
import sharedClasses.Car_shared;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewCarController {

    public static Thread prev_thread = null;
    public static List<Car_shared> carSharedList = new ArrayList<>();
    ObservableList<ModifiedCar> data;

    @FXML
    private TableView<ModifiedCar> table_of_users;

    @FXML
    private TableColumn<ModifiedCar, ImageView> car_image;

    @FXML
    private TableColumn<ModifiedCar, String> registration_number;

    @FXML
    private TableColumn<ModifiedCar, String> colour1;

    @FXML
    private TableColumn<ModifiedCar, String> colour2;

    @FXML
    private TableColumn<ModifiedCar, String> colour3;

    @FXML
    private TableColumn<ModifiedCar, String> car_make;

    @FXML
    private TableColumn<ModifiedCar, String> car_model;

    @FXML
    private TableColumn<ModifiedCar, String> car_price;

    @FXML
    private TableColumn<ModifiedCar, Integer> available_quantity;

    @FXML
    private TableColumn<ModifiedCar, Integer> year_made;

    private void initializeColumns() //Getting table data from car class
    {
        car_image.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        registration_number.setCellValueFactory(new PropertyValueFactory<>("CarReg"));
        colour1.setCellValueFactory(new PropertyValueFactory<>("Colour1"));
        colour2.setCellValueFactory(new PropertyValueFactory<>("Colour2"));
        colour3.setCellValueFactory(new PropertyValueFactory<>("Colour3"));
        car_make.setCellValueFactory(new PropertyValueFactory<>("CarMake"));
        car_model.setCellValueFactory(new PropertyValueFactory<>("CarModel"));
        car_price.setCellValueFactory(new PropertyValueFactory<>("Price"));
        available_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        year_made.setCellValueFactory(new PropertyValueFactory<>("YearMade"));
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

                            carSharedList = (List<Car_shared>) Main.connector_3_for_car_list_update.getObjectInputStream().readObject();

                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }

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
        initializeColumns();
        populateTable();
    }

}
