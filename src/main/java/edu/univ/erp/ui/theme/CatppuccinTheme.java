package edu.univ.erp.ui.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;

public class CatppuccinTheme extends FlatDarkLaf {

    public static final String NAME = "Catppuccin Mocha";

    // Core palette
    public static final Color BASE      = Color.decode("#1e1e2e");
    public static final Color MANTLE    = Color.decode("#181825");
    public static final Color CRUST     = Color.decode("#11111b");

    public static final Color TEXT      = Color.decode("#cdd6f4");
    public static final Color SUBTEXT0  = Color.decode("#a6adc8");

    public static final Color SURFACE0  = Color.decode("#313244");
    public static final Color SURFACE1  = Color.decode("#45475a");
    public static final Color SURFACE2  = Color.decode("#585b70");

    public static final Color BLUE      = Color.decode("#89b4fa");
    public static final Color MAUVE     = Color.decode("#cba6f7");
    public static final Color RED       = Color.decode("#f38ba8");
    public static final Color GREEN     = Color.decode("#a6e3a1");
    public static final Color YELLOW    = Color.decode("#f9e2af");

    public static boolean setup() {
        return FlatLaf.setup(new CatppuccinTheme());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public UIDefaults getDefaults() {
        UIDefaults UIManager = super.getDefaults();


        UIManager.put("control", BASE);
        UIManager.put("Panel.background", BASE);
        UIManager.put("Component.focusColor", MAUVE);

        // Labels
        UIManager.put("Label.foreground", TEXT);

        // Buttons
        UIManager.put("Button.background", SURFACE0);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.hoverBackground", SURFACE1);
        UIManager.put("Button.pressedBackground", SURFACE2);
        UIManager.put("Button.focusedBorderColor", MAUVE);
        UIManager.put("Button.borderColor", SURFACE1);

        // TextFields
        UIManager.put("TextField.background", SURFACE0);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.caretForeground", BLUE);
        UIManager.put("TextField.selectionBackground", MAUVE);
        UIManager.put("TextField.selectionForeground", BASE);
        UIManager.put("TextField.borderColor", SURFACE2);

        // Tables
        UIManager.put("Table.background", BASE);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.selectionBackground", BLUE);
        UIManager.put("Table.selectionForeground", BASE);
        UIManager.put("Table.gridColor", SURFACE1);

        // Table header
        UIManager.put("TableHeader.background", MANTLE);
        UIManager.put("TableHeader.foreground", TEXT);
        UIManager.put("TableHeader.bottomSeparatorColor", SURFACE2);

        // Scrollbars
        UIManager.put("ScrollBar.track", MANTLE);
        UIManager.put("ScrollBar.thumb", SURFACE1);
        UIManager.put("ScrollBar.thumbHover", SURFACE2);
        UIManager.put("ScrollBar.thumbPressed", MAUVE);

        // ComboBox
        UIManager.put("ComboBox.background", SURFACE0);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.selectionBackground", MAUVE);
        UIManager.put("ComboBox.selectionForeground", BASE);

        // Menus
        UIManager.put("Menu.foreground", TEXT);
        UIManager.put("Menu.background", BASE);
        UIManager.put("MenuItem.foreground", TEXT);
        UIManager.put("MenuItem.background", BASE);
        UIManager.put("MenuItem.selectionBackground", BLUE);
        UIManager.put("MenuItem.selectionForeground", BASE);

        return UIManager;

    }
}
