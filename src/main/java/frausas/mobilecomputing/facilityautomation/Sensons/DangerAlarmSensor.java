package frausas.mobilecomputing.facilityautomation.Sensons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.dto.LightSensorRequest;
import frausas.mobilecomputing.facilityautomation.sensorstate.DangerAlaramSensorState;
import frausas.mobilecomputing.facilityautomation.sensorstate.LightSensorState;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;

import java.util.Timer;
import java.util.TimerTask;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;

public class DangerAlarmSensor extends ConcurrentCoapResource {

    public static DangerAlaramSensorState sensorState;

    public DangerAlarmSensor(String name) {
        super(name, SINGLE_THREADED);
        sensorState = new DangerAlaramSensorState();

        setObservable(true);
        setObserveType(CoAP.Type.CON);
        getAttributes().setObservable();
        Timer timer = new Timer();
        timer.schedule(new UpdateTask(), 0, 1000);
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
            String json = exchange.getRequestText();
            try {
                ObjectMapper mapper = new ObjectMapper();
                LightSensorRequest lightSensorRequest = mapper.readValue(json, LightSensorRequest.class);
                sensorState.setOn(lightSensorRequest.isOn());
                String responseJson = mapper.writeValueAsString(sensorState);
                exchange.respond(CREATED, responseJson);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            exchange.respond(CREATED);
        }
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            changed();
        }
    }
}