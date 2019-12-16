package frausas.mobilecomputing.facilityautomation.Sensons;

import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.dto.SecuritySensorRequest;
import frausas.mobilecomputing.facilityautomation.sensorstate.SecuritySensorState;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;

public class SecurityAccessSensor extends ConcurrentCoapResource {

    public static SecuritySensorState sensorState;

    public SecurityAccessSensor(String name) {
        super(name, SINGLE_THREADED);
        sensorState = new SecuritySensorState();
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        if (exchange.getRequestOptions().isContentFormat(MediaTypeRegistry.APPLICATION_JSON)) {

            String json = exchange.getRequestText();
            try {
                ObjectMapper mapper = new ObjectMapper();
                SecuritySensorRequest requestDto = mapper.readValue(json, SecuritySensorRequest.class);

                if(requestDto.getPeopleEntered() > 0) {
                    sensorState.setTotalPeople(sensorState.getTotalPeople() + requestDto.getPeopleEntered());
                }
                if(requestDto.getPeopleExited() > 0 && sensorState.getTotalPeople() > 0) {
                    sensorState.setTotalPeople(sensorState.getTotalPeople() - requestDto.getPeopleExited());
                }
                if(sensorState.getTotalPeople() > 0 && requestDto.isThief() == true) {
                    sensorState.setHasThief(true);
                } else {
                    sensorState.setHasThief(false);
                }
                String responseJson = mapper.writeValueAsString(sensorState);
                exchange.respond(CREATED, responseJson);

            } catch (Exception ex) {

            }
        } else {
            exchange.respond(CREATED);
        }
    }
}
