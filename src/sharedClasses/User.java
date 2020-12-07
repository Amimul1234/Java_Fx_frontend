package sharedClasses;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 6529685098267757690L;
     private String name;
     private byte[] image;
     private String role;
     private String password;

    public User() {
    }

    public User(String name, byte[] image, String role, String password) {
        this.name = name;
        this.image = image;
        this.role = role;
        this.password = password;
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
}
