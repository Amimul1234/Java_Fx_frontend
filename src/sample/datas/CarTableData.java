package sample.datas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.login.Main;
import sample.manufacturer.ModifiedCar;
import sharedClasses.Car_shared;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CarTableData {
    private List<Car_shared> carSharedList;
    private List<ModifiedCar> modifiedCarList;
    private ObservableList<ModifiedCar> data;

    private static CarTableData carTableData = null;

    private CarTableData() {
        carSharedList = new ArrayList<>();
        modifiedCarList = new ArrayList<>();
        data = FXCollections.observableArrayList();

        populate();
    }

    public static CarTableData getInstance()
    {
        if(carTableData == null)
            carTableData = new CarTableData();

        return carTableData;
    }

    private void populate() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    try {
                        carSharedList.clear();
                        carSharedList.addAll((List<Car_shared>) Main.connector_3_for_car_list_update.getObjectInputStream().readObject());
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }

                    modifiedCarList.clear();

                    for(Car_shared car_shared : carSharedList)
                    {
                        modifiedCarList.add(new ModifiedCar(car_shared.getQuantity(), car_shared.getCarReg(), car_shared.getYearMade(),
                                car_shared.getColour1(), car_shared.getColour2(), car_shared.getColour3(), car_shared.getCarMake(),
                                car_shared.getCarModel(), car_shared.getPrice(), car_shared.getCarImage()));
                    }

                    data.clear();
                    data.addAll(modifiedCarList);
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public ObservableList<ModifiedCar> getData() {
        return data;
    }
}
