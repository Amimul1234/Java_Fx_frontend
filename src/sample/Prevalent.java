package sample;

import java.util.ArrayList;
import java.util.List;

public class Prevalent {
    private static String user_name;
    private static String image_url;
    private static String role;
    private static List<String> options = new ArrayList<>();

    public static String getUser_name() {
        return user_name;
    }

    public static void setUser_name(String user_name) {
        Prevalent.user_name = user_name;
    }

    public static String getImage_url() {
        return image_url;
    }

    public static void setImage_url(String image_url) {
        Prevalent.image_url = image_url;
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String role) {
        Prevalent.role = role;
    }

    public static List<String> getOptions() {
        return options;
    }

    public static void setOptions(List<String> options) {
        Prevalent.options = options;
    }
}
