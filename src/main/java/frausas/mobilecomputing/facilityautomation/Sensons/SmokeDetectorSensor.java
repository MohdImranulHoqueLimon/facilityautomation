package frausas.mobilecomputing.facilityautomation.Sensons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.dto.SmokeRequestDto;
import frausas.mobilecomputing.facilityautomation.sensorstate.SmokeDetectorSensorState;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;

public class SmokeDetectorSensor extends ConcurrentCoapResource {

    private SmokeDetectorSensorState smokeDetectorSensorState;

    public SmokeDetectorSensor(String name) {

        super(name, 1);
        smokeDetectorSensorState = new SmokeDetectorSensorState();

        setObservable(true);
        setObserveType(CoAP.Type.CON);
        getAttributes().setObservable();
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            exchange.respond(mapper.writeValueAsString(smokeDetectorSensorState));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        if (exchange.getRequestOptions().isContentFormat(MediaTypeRegistry.APPLICATION_JSON)) {
            String jsonRequest = exchange.getRequestText();
            try {
                ObjectMapper mapper = new ObjectMapper();
                SmokeRequestDto requestDto = mapper.readValue(jsonRequest, SmokeRequestDto.class);

                smokeDetectorSensorState.setSmoke(requestDto.isFire());

                changed();
                String responseJson = mapper.writeValueAsString(smokeDetectorSensorState);
                exchange.respond(CREATED, responseJson);
            } catch (Exception ex) {

            }
        } else {
            exchange.respond(CREATED);
        }
    }
}
