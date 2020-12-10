package sample.admin;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class Modified{
    private Integer id;
    private String name;
    private byte[] image;
    private String role;
    private String password;
    private List<String> actions;
    private ImageView imageView;

    public Modified() {
        actions = new ArrayList<>();
    }

    public Modified(String name, byte[] image, String role, String password, List<String> actions, int id) {
        this.name = name;
        this.image = image;
        this.role = role;
        this.password = password;
        this.actions = actions;
        this.id = id;
        imageView = new ImageView(new Image(new ByteArrayInputStream(image)));
        imageView.setFitHeight(100);
        imageView.setFitWidth(130);
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
        imageView = new ImageView(new Image(new ByteArrayInputStream(image)));
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setByteArraySize(int size)
    {
        image = new byte[size];
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
