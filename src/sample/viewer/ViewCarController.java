package sample.viewer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import sample.datas.CarTableData;
import sample.login.Main;
import sample.manufacturer.ModifiedCar;
import sharedClasses.Car_shared;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ViewCarController {

    private List<Car_shared> carSharedList = new ArrayList<>();
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

    @FXML
    private Button back_to_main_menu_button;

    @FXML
    void back_to_main_menu(ActionEvent event) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                ObjectOutputStream objectOutputStream = Main.connector.getObjectOutputStream();
                try {
                    objectOutputStream.writeObject(new String("back to main menu"));
                    objectOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/role_scene.fxml"));
                        Stage stage = (Stage) back_to_main_menu_button.getScene().getWindow();
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
        initializeColumns();
        populateTable();
    }

}
