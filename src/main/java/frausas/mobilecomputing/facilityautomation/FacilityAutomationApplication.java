package frausas.mobilecomputing.facilityautomation;

import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.Sensons.SecurityAccessSensor;
import frausas.mobilecomputing.facilityautomation.sensorstate.SecuritySensorState;
import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
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

        CoapClient client = new CoapClient("coap://localhost:" + SensorConstants.SECURITY_ACCESS_SENSOR_PORT + "/" + SensorConstants.SECURITY_ACCESS_SENSOR_ENDPOINT);
        CoapObserveRelation relation = client.observe(
                new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        String content = response.getResponseText();
                        System.out.println("NOTIFICATION: " + content);
                    }

                    @Override
                    public void onError() {
                        System.err.println("OBSERVING FAILED (press enter to exit)");
                    }
                });
    }

}
