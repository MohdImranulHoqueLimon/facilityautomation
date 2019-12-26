package frausas.mobilecomputing.facilityautomation;

import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.Sensons.DangerAlarmSensor;
import frausas.mobilecomputing.facilityautomation.Sensons.LightSensor;
import frausas.mobilecomputing.facilityautomation.Sensons.SecurityAccessSensor;
import frausas.mobilecomputing.facilityautomation.dto.AlarmSensorRequest;
import frausas.mobilecomputing.facilityautomation.dto.LightSensorRequest;
import frausas.mobilecomputing.facilityautomation.sensorstate.AllSensorState;
import frausas.mobilecomputing.facilityautomation.sensorstate.DangerAlaramSensorState;
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
    private static boolean alarmRunning = false;

    public static void main(String[] args) {

        allSensorState = new AllSensorState();

        SpringApplication.run(FacilityAutomationApplication.class, args);

        CoapClient securityClient = new CoapClient("coap://localhost:" + SensorConstants.SECURITY_ACCESS_SENSOR_PORT + "/" + SensorConstants.SECURITY_ACCESS_SENSOR_ENDPOINT);
        CoapServer securityServer = new CoapServer(SensorConstants.SECURITY_ACCESS_SENSOR_PORT);
        securityServer.add(new SecurityAccessSensor(SensorConstants.SECURITY_ACCESS_SENSOR_ENDPOINT));
        securityServer.start();

        CoapServer alarmSensorServer = new CoapServer(SensorConstants.ALARM_SENSOR_PORT);
        alarmSensorServer.add(new DangerAlarmSensor(SensorConstants.ALARM_SENSOR_ENDPOINT));
        alarmSensorServer.start();

        CoapClient lightClient = new CoapClient("coap://localhost:" + SensorConstants.LIGHT_SENSOR_PORT + "/" + SensorConstants.LIGHT_SENSOR_ENDPOINT);
        CoapServer lightsServer = new CoapServer(SensorConstants.LIGHT_SENSOR_PORT);
        lightsServer.add(new LightSensor(SensorConstants.LIGHT_SENSOR_ENDPOINT));
        lightsServer.start();

        ObjectMapper mapper = new ObjectMapper();

        CoapObserveRelation securityObserver = securityClient.observe(
                new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        String jsonResponse = response.getResponseText();
                        System.out.println(jsonResponse);
                        try {
                            SecuritySensorState securitySensorState = mapper.readValue(jsonResponse, SecuritySensorState.class);
                            changeLightState(securitySensorState.getTotalPeople());
                            allSensorState.setSecuritySensorState(securitySensorState);

                            if(securitySensorState.isHasThief() && alarmRunning == false) {
                                changeAlarmState(securitySensorState.isHasThief());
                                alarmRunning = true;
                            }

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
                            LightSensorState lightSensorState = mapper.readValue(jsonResponse, LightSensorState.class);
                            allSensorState.setLightSensorState(lightSensorState);
                        } catch (Exception ex) {

                        }
                    }
                    @Override
                    public void onError() {
                        System.err.println("OBSERVING FAILED (press enter to exit)");
                    }
                });
    }

    public static void changeAlarmState(boolean onOrOff) {
        try {
            new Thread() {
                public void run() {
                    try {
                        CoapClient alarmSensorClient = new CoapClient("coap://localhost:" + SensorConstants.ALARM_SENSOR_PORT + "/" + SensorConstants.ALARM_SENSOR_ENDPOINT);
                        ObjectMapper mapper = new ObjectMapper();
                        AlarmSensorRequest request = new AlarmSensorRequest();
                        request.setOn(onOrOff);
                        String jsonString = mapper.writeValueAsString(request);

                        CoapResponse coapResponse = alarmSensorClient.post(jsonString, MediaTypeRegistry.APPLICATION_JSON);
                        jsonString = coapResponse.getResponseText();

                        System.out.println(jsonString);

                        DangerAlaramSensorState dangerAlaramSensorState = mapper.readValue(jsonString, DangerAlaramSensorState.class);
                        allSensorState.setAlaramSensorState(dangerAlaramSensorState);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
