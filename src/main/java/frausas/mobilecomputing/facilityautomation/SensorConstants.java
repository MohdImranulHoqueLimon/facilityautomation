package frausas.mobilecomputing.facilityautomation;

import java.util.Random;

public class SensorConstants {
    public static final int SECURITY_ACCESS_SENSOR_PORT = 5683;
    public static final int ALARM_SENSOR_PORT = 5684;
    public static final int LIGHT_SENSOR_PORT = 5685;

    public static final String SECURITY_ACCESS_SENSOR_ENDPOINT = "security-access";
    public static final String ALARM_SENSOR_ENDPOINT = "security-alarm";
    public static final String LIGHT_SENSOR_ENDPOINT = "light";

    public static final String SECURITY_PIN = "13579";

    public static int getRandomNumberInRange(int min, int max, int currentTotalPeople) {

        int[] numbers = {5, -3, -2, 1, 4, 7, -11, -4, 6};
        if(currentTotalPeople > 5) return -5;

        Random r = new Random();
        int randomIndex = r.nextInt((5 - 0) + 1) + 0;

        return numbers[randomIndex];
    }

}
