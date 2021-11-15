package pl.sages.javadevpro.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class Utils {

    public static int readPortFromPropertyOrReturnDefault(String propertyName, int defaultPort, int minRange, int maxRange){
        String propertyValue = System.getProperty(propertyName);
        if(propertyValue != null) {
            int temp = Integer.parseInt(propertyValue);
            if (temp >= minRange && temp <= maxRange) {
                return temp;
            }
        }
        return defaultPort;
    }

}
