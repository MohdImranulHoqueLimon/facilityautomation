package frausas.mobilecomputing.facilityautomation;

import frausas.mobilecomputing.facilityautomation.Sensons.SecurityAccessSensor;
import org.eclipse.californium.core.CoapServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FacilityAutomationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacilityAutomationApplication.class, args);

        CoapServer securityServer = new CoapServer(SensorConstants.SECURITY_ACCESS_SENSOR_PORT);
        securityServer.add(new SecurityAccessSensor(SensorConstants.SECURITY_ACCESS_SENSOR_ENDPOINT));
        securityServer.start();

        CoapServer alarmServer = new CoapServer(SensorConstants.ALARM_SENSOR_PORT);
        alarmServer.add(new SecurityAccessSensor(SensorConstants.ALARM_SENSOR_ENDPOINT));
        alarmServer.start();
    }

}
