package sample.viewer;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sharedClasses.Car_shared;

import java.io.ByteArrayInputStream;

public class Car_details {

    private Car_shared car_shared = null;

    @FXML
    private ImageView car_image;

    @FXML
    private TextField car_reg_number;

    @FXML
    private TextField car_make;

    @FXML
    private TextField car_model;

    @FXML
    private TextField car_colour_1;

    @FXML
    private TextField car_colour_2;

    @FXML
    private TextField car_colour_3;

    @FXML
    private TextField car_price;

    @FXML
    private TextField car_year_made;

    @FXML
    private TextField car_quantity;

    public void setCar(Car_shared car_shared) {
        this.car_shared = car_shared;
        init();
    }

    public void init()
    {
        car_image.setImage(new Image(new ByteArrayInputStream(car_shared.getCarImage())));
        car_reg_number.setText(car_shared.getCarReg());
        car_make.setText(car_shared.getCarMake());
        car_model.setText(car_shared.getCarModel());
        car_colour_1.setText(car_shared.getColour1());
        car_colour_2.setText(car_shared.getColour2());
        car_colour_3.setText(car_shared.getColour3());
        car_price.setText(String.valueOf(car_shared.getPrice()));
        car_year_made.setText(String.valueOf(car_shared.getYearMade()));
        car_quantity.setText(String.valueOf(car_shared.getQuantity()));
    }
}
