package frausas.mobilecomputing.facilityautomation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.dto.RequestDto;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("iot/gateway/")
public class ApiGateway {

    @PostMapping("send")
    public RequestDto send(@RequestBody RequestDto requestDto) {

        CoapClient client = new CoapClient("coap://localhost:8085/example");
        //requestDto.setName("My input =" + requestDto.getName() + " From coap= " + client.get().getResponseText());

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
}
