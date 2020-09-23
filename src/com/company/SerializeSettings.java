
package com.company;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * This class serializes the Settings class and writes it into a file
 * every time the program runs to read it later .
 *
 * @author Mina Beiki
 * @version 2020.05.12
 */

public class SerializeSettings {

    /**
     * Serializes the settings and writes it into a  file . ( settings.txt )
     *
     * @param settings Settings settings to be serialized
     */

    public void serialize(Settings settings) {
        try {
            FileOutputStream file = new FileOutputStream("settings.txt");
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(settings);
            out.close();
            file.close();
            System.out.println("serialized");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}




