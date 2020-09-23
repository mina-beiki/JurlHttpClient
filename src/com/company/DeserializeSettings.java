
package com.company;

import java.io.*;


/**
 * Deserializes an object from Settings class and reads it from the file which
 * it has been serialized into it before .
 *
 * @author Mina Beiki
 * @version 2020.05.12
 * <p>
 * Deserializes the settings and reads it from the file it has been serialized into  ( settings.txt )
 * and at last returns the Settings .
 * @return Settings settings which has been read from file
 */

public class DeserializeSettings {
/**
 * Deserializes the settings and reads it from the file it has been serialized into  ( settings.txt )
 * and at last returns the Settings .
 * @return Settings settings which has been read from file
 */

    public Settings deserialize(){
        Settings s = null;
        try {
            FileInputStream file = new FileInputStream("settings.txt");
            ObjectInputStream in = new ObjectInputStream(file);
            s = (Settings) in.readObject();
            in.close();
            file.close();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }
        System.out.println("theme : "+s.getTheme());
        System.out.println("exit : "+s.getExitOperation());
        System.out.println("follow redirect : "+s.getFollowRedirect());
        return s ;

    }

}

