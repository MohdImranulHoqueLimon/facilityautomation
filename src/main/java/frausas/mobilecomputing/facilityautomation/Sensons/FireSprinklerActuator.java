package frausas.mobilecomputing.facilityautomation.Sensons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.dto.FireSprinklerRequest;
import frausas.mobilecomputing.facilityautomation.sensorstate.FireSprinklerState;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;

import java.util.Timer;
import java.util.TimerTask;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;

public class FireSprinklerActuator extends ConcurrentCoapResource {

    private FireSprinklerState sensorState;
    ObjectMapper mapper = new ObjectMapper();

    public FireSprinklerActuator(String name) {
        super(name, 2);
        sensorState = new FireSprinklerState();

        mapper = new ObjectMapper();
        setObservable(true);
        setObserveType(CoAP.Type.CON);
        getAttributes().setObservable();
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(sensorState);
            exchange.respond(jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();
        if (exchange.getRequestOptions().isContentFormat(MediaTypeRegistry.APPLICATION_JSON)) {
            try {
                String jsonRequest = exchange.getRequestText();
                FireSprinklerRequest request = mapper.readValue(jsonRequest, FireSprinklerRequest.class);
                sensorState.setOn(request.isOn());

                String responseJson = mapper.writeValueAsString(sensorState);
                exchange.respond(CREATED, responseJson);
                changed();
                if(sensorState.isOn() == true) {
                    Timer timer = new Timer();
                    timer.schedule(new StopFireSprinkler(), 10000);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            exchange.respond(CREATED);
        }
    }

    private class StopFireSprinkler extends TimerTask {
        @Override
        public void run() {
            sensorState.setOn(false);
            changed();
        }
    }
}
