package edu.univ.erp;
import java.sql.Connection;

import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.data.AuthDB;


import edu.univ.erp.ui.LoginWindow;

public class Main {
    public static void main(String[] args) {
        LoginWindow.show();
        System.out.println(PasswordHasher.hash("admin123"));


        try (Connection conn = AuthDB.getConnection()) {
            System.out.println("Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
