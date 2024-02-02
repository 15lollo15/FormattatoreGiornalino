package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.gui.Controller;

import java.io.IOException;

public class Main {
    static final int MAX_PAGE = 3;
    public static void main(String[] args) {
        FlatLightLaf.setup();
        Controller c = Controller.getInstance();
        c.showWindow();
    }

}