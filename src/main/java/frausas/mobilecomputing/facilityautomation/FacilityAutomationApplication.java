package frausas.mobilecomputing.facilityautomation;

import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.Sensons.LightSensor;
import frausas.mobilecomputing.facilityautomation.Sensons.SecurityAccessSensor;
import frausas.mobilecomputing.facilityautomation.dto.LightSensorRequest;
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

        CoapServer lightsServer = new CoapServer(SensorConstants.LIGHT_SENSOR_PORT);
        lightsServer.add(new LightSensor(SensorConstants.LIGHT_SENSOR_ENDPOINT));
        lightsServer.start();

        CoapClient securityClient = new CoapClient("coap://localhost:" + SensorConstants.SECURITY_ACCESS_SENSOR_PORT + "/" + SensorConstants.SECURITY_ACCESS_SENSOR_ENDPOINT);
        CoapClient lightClient = new CoapClient("coap://localhost:" + SensorConstants.LIGHT_SENSOR_PORT + "/" + SensorConstants.LIGHT_SENSOR_ENDPOINT);

        CoapObserveRelation securityObserver = securityClient.observe(
                new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        String jsonResponse = response.getResponseText();
                        System.out.println("NOTIFICATION: " + jsonResponse);
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            SecuritySensorState securitySensorState = mapper.readValue(jsonResponse, SecuritySensorState.class);

                            if(securitySensorState.getTotalPeople() == 0) {

                                LightSensorRequest request = new LightSensorRequest();
                                request.setOn(false);
                                String jsonString = mapper.writeValueAsString(request);

                                CoapResponse coapResponse = lightClient.post(jsonString, MediaTypeRegistry.APPLICATION_JSON);
                                jsonString = coapResponse.getResponseText();
                            }
                        } catch (Exception ex) {

                        }
                    }

                    @Override
                    public void onError() {
                        System.err.println("OBSERVING FAILED (press enter to exit)");
                    }
                });

        CoapObserveRelation relation = lightClient.observe(
                new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        String jsonResponse = response.getResponseText();
                        System.out.println("light notfication: " + jsonResponse);
                    }

                    @Override
                    public void onError() {
                        System.err.println("OBSERVING FAILED (press enter to exit)");
                    }
                });
    }

}
