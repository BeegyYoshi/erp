package edu.univ.erp;

import edu.univ.erp.data.AuthDB;
import edu.univ.erp.MainFrame;
import edu.univ.erp.ui.theme.CatppuccinTheme;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        /*
        try {
            javax.swing.UIManager.setLookAndFeel(new CatppuccinTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }

         */

        // Launch the single Frame (with login screen inside)
        new MainFrame();

        // Optional: DB connectivity test
        try (Connection conn = AuthDB.getConnection()) {
            System.out.println("Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
