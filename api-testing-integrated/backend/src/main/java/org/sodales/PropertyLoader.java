package org.sodales;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

    // This method is used to load the properties file.
    public static String loadProperties(String propertyname){
        //propertyname="Authorization";
        try (InputStream input = new FileInputStream("src/main/resources/framework.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            return prop.getProperty(propertyname);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
