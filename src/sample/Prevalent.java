package sample;

import java.util.List;

public class Prevalent{
    private static String name;
    private static byte[] image;
    private static String role;
    private static String password;
    private static List<String> actions;

    public Prevalent() {
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Prevalent.name = name;
    }

    public static byte[] getImage() {
        return image;
    }

    public static void setImage(byte[] image) {
        Prevalent.image = image;
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String role) {
        Prevalent.role = role;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Prevalent.password = password;
    }

    public static List<String> getActions() {
        return actions;
    }

    public static void setActions(List<String> actions) {
        Prevalent.actions = actions;
    }
}
