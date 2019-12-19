package frausas.mobilecomputing.facilityautomation;

import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.Sensons.LightSensor;
import frausas.mobilecomputing.facilityautomation.Sensons.SecurityAccessSensor;
import frausas.mobilecomputing.facilityautomation.dto.LightSensorRequest;
import frausas.mobilecomputing.facilityautomation.sensorstate.AllSensorState;
import frausas.mobilecomputing.facilityautomation.sensorstate.LightSensorState;
import frausas.mobilecomputing.facilityautomation.sensorstate.SecuritySensorState;
import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FacilityAutomationApplication {

    @Autowired
    public static AllSensorState allSensorState;

    public static void main(String[] args) {

        allSensorState = new AllSensorState();

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
        ObjectMapper mapper = new ObjectMapper();

        CoapObserveRelation securityObserver = securityClient.observe(
                new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        String jsonResponse = response.getResponseText();
                        //System.out.println("NOTIFICATION: " + jsonResponse);
                        try {
                            SecuritySensorState securitySensorState = mapper.readValue(jsonResponse, SecuritySensorState.class);
                            changeLightState(securitySensorState.getTotalPeople());

                            allSensorState.setSecuritySensorState(securitySensorState);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError() {
                        System.err.println("OBSERVING FAILED (press enter to exit)");
                    }

                    private void changeLightState(int totalPeople) {
                        boolean onOrOff = totalPeople > 0;
                        try {
                            new Thread() {
                                public void run() {
                                    try {
                                        LightSensorRequest request = new LightSensorRequest();
                                        request.setOn(onOrOff);
                                        String jsonString = mapper.writeValueAsString(request);
                                        CoapResponse coapResponse = lightClient.post(jsonString, MediaTypeRegistry.APPLICATION_JSON);
                                        jsonString = coapResponse.getResponseText();
                                        LightSensorState lightSensorState = mapper.readValue(jsonString, LightSensorState.class);
                                        allSensorState.setLightSensorState(lightSensorState);
                                        //System.out.println("NOTIFICATION---: " + jsonString);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }.start();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

        CoapObserveRelation relation = lightClient.observe(
                new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        try {
                            String jsonResponse = response.getResponseText();
                            //System.out.println("light state: " + jsonResponse);
                            LightSensorState lightSensorState = mapper.readValue(jsonResponse, LightSensorState.class);
                            allSensorState.setLightSensorState(lightSensorState);
                            System.out.println(mapper.writeValueAsString(allSensorState));
                        } catch (Exception ex) {

                        }
                    }
                    @Override
                    public void onError() {
                        System.err.println("OBSERVING FAILED (press enter to exit)");
                    }
                });
    }

    private void changeLightState(boolean state) {
        CoapClient lightClient = new CoapClient("coap://localhost:" + SensorConstants.LIGHT_SENSOR_PORT + "/" + SensorConstants.LIGHT_SENSOR_ENDPOINT);
        try {
            ObjectMapper mapper = new ObjectMapper();
            LightSensorRequest request = new LightSensorRequest();
            request.setOn(state);
            String jsonString = mapper.writeValueAsString(request);
            CoapResponse coapResponse = lightClient.post(jsonString, MediaTypeRegistry.APPLICATION_JSON);
            jsonString = coapResponse.getResponseText();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
