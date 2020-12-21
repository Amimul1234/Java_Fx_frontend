package sample.viewer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import sample.manufacturer.ModifiedCar;
import sharedClasses.Car_shared;
import java.util.ArrayList;
import java.util.List;

public class CarDetailsListForSearch{

    private List<Car_shared> carSharedList;
    private ObservableList<ModifiedCar> data;

    @FXML
    private TableView<ModifiedCar> table_of_cars;

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
            }
        }).start();

    }

    public void init()
    {
        initializeColumns();
        populateTable();
    }

    public void setCars(List<Car_shared> car_sharedList) {
        this.carSharedList = car_sharedList;
        init();
    }
}
