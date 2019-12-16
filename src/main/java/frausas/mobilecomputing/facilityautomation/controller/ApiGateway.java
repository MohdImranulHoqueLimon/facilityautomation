package frausas.mobilecomputing.facilityautomation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.Sensons.MyResource;
import frausas.mobilecomputing.facilityautomation.SensorConstants;
import frausas.mobilecomputing.facilityautomation.dto.RequestDto;
import frausas.mobilecomputing.facilityautomation.dto.SecuritySensorRequest;
import frausas.mobilecomputing.facilityautomation.sensorstate.SecuritySensorState;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("gateway/")
public class ApiGateway {

    @PostMapping("send")
    public RequestDto send(@RequestBody RequestDto requestDto) {

        CoapClient client = new CoapClient("coap://localhost:8085/example");

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(requestDto);

            CoapResponse coapResponse = client.post(jsonString, MediaTypeRegistry.APPLICATION_JSON);
            jsonString = coapResponse.getResponseText();
            requestDto = mapper.readValue(jsonString, RequestDto.class);


        } catch (Exception ex) {

        }
        return requestDto;
    }

    @PostMapping("security")
    public SecuritySensorState security(@RequestBody SecuritySensorRequest requestDto) {

        CoapClient client = new CoapClient("coap://localhost:" + SensorConstants.SECURITY_ACCESS_SENSOR_PORT + "/" + SensorConstants.SECURITY_ACCESS_SENSOR_ENDPOINT);
        SecuritySensorState securitySensorState = new SecuritySensorState();

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(requestDto);

            CoapResponse coapResponse = client.post(jsonString, MediaTypeRegistry.APPLICATION_JSON);
            jsonString = coapResponse.getResponseText();
            securitySensorState = mapper.readValue(jsonString, SecuritySensorState.class);

        } catch (Exception ex) {

        }
        return securitySensorState;
    }

    @GetMapping("start")
    public void start() {
        CoapServer server = new CoapServer(8085);
        server.add(new MyResource("example"));
        server.start();
    }


}
