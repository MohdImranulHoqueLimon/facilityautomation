package frausas.mobilecomputing.facilityautomation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.FacilityAutomationApplication;
import frausas.mobilecomputing.facilityautomation.SensorConstants;
import frausas.mobilecomputing.facilityautomation.dto.SecuritySensorRequest;
import frausas.mobilecomputing.facilityautomation.dto.SmokeRequestDto;
import frausas.mobilecomputing.facilityautomation.sensorstate.AllSensorState;
import frausas.mobilecomputing.facilityautomation.sensorstate.SecuritySensorState;
import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gateway/")
public class IotGateway {

    @GetMapping("get-all-data")
    public AllSensorState getData() {
        return FacilityAutomationApplication.allSensorState;
    }

    @PostMapping("security")
    public SecuritySensorState security(@RequestBody SecuritySensorRequest requestDto) {

        CoapClient client = new CoapClient("coap://localhost:" + SensorConstants.SECURITY_ACCESS_SENSOR_PORT + "/" + SensorConstants.SECURITY_ACCESS_SENSOR_ENDPOINT);
        SecuritySensorState securitySensorState = new SecuritySensorState();

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(requestDto);

            if (requestDto.isEvacuate() == true) {
                FacilityAutomationApplication.changeAlarmState(false);
            }

            CoapResponse coapResponse = client.post(jsonString, MediaTypeRegistry.APPLICATION_JSON);
            jsonString = coapResponse.getResponseText();
            securitySensorState = mapper.readValue(jsonString, SecuritySensorState.class);
        } catch (Exception ex) {

        }
        return securitySensorState;
    }

    @PostMapping("stop-alarming")
    public SecuritySensorState stopAlarming(@RequestBody SecuritySensorRequest requestDto) {
        FacilityAutomationApplication.changeAlarmState(false);
        return null;
    }

    @PostMapping("start-fire")
    public SecuritySensorState startFire(@RequestBody SmokeRequestDto smokeRequestDto) {
        try {
            CoapClient smokeClient = new CoapClient("coap://localhost:" + SensorConstants.SMOKE_DETECTOR_SENSOR_PORT + "/" + SensorConstants.SMOKE_DETECTOR_SENSOR_ENDPOINT);
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(smokeRequestDto);

            CoapResponse coapResponse = smokeClient.post(jsonString, MediaTypeRegistry.APPLICATION_JSON);
            jsonString = coapResponse.getResponseText();
        } catch (Exception ex) {

        }

        return null;
    }

}
