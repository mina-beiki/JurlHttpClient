package com.company;

/**
 * This is a HTTP Client program which is capable of sending requests and
 * getting responses .
 * This class is used to test the program with gui interface (UI) and
 * runs it through it .
 *
 * @author Mina Beiki
 * @version 2020.06.04
 */
public class MainGUI {
    public static void main(String[] args) {

      ApplicationGUI appGUI = new ApplicationGUI();
        appGUI.generateMenuBar();
        appGUI.generatePanel1();
        appGUI.generatePanel2();
        appGUI.generatePanel3();
        appGUI.showGUI();


    }
}
