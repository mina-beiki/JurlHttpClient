package com.company;

import java.io.File;
import java.net.MalformedURLException;

/**
 * This is a HTTP Client program which is capable of sending requests and
 * getting responses .
 * This class is for running the program and testing different methods in console
 * and using input from user .
 *
 *
 * @author Mina Beiki
 * @version 2020.06.04
 */
public class MainConsole {

    private static final String RESPONSES_PATH = "./responses/";
    private static final String REQUESTS_PATH = "./requests/";
    private static final String DL_PATH = "./downloads/";

    public static void main(String[] args) {

        boolean isSuccessful1 = new File(RESPONSES_PATH).mkdirs();
        System.out.println("Creating " + RESPONSES_PATH + " directory is successful: " + isSuccessful1);
        boolean isSuccessful2 = new File(REQUESTS_PATH).mkdirs();
        System.out.println("Creating " + REQUESTS_PATH + " directory is successful: " + isSuccessful2);
        boolean isSuccessful3 = new File(DL_PATH).mkdirs();
        System.out.println("Creating " + DL_PATH + " directory is successful: " + isSuccessful3);


        //used for phase 2 (for getting only input) :

        Input.getInput();
        while (!(Input.checkExit())) {
            try {
                RequestHandler request = new RequestHandler(Input.getSplit());
                request.doRequest();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Input.getInput();
        }

    }

}
