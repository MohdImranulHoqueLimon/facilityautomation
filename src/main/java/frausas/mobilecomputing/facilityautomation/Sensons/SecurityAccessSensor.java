package frausas.mobilecomputing.facilityautomation.Sensons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frausas.mobilecomputing.facilityautomation.SensorConstants;
import frausas.mobilecomputing.facilityautomation.dto.SecuritySensorRequest;
import frausas.mobilecomputing.facilityautomation.sensorstate.SecuritySensorState;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;

import java.util.Timer;
import java.util.TimerTask;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;

public class SecurityAccessSensor extends ConcurrentCoapResource {

    public static SecuritySensorState sensorState;

    public SecurityAccessSensor(String name) {

        super(name, 1);
        sensorState = new SecuritySensorState();

        setObservable(true);
        setObserveType(CoAP.Type.CON);
        getAttributes().setObservable();

        Timer timer = new Timer();
        timer.schedule(new UpdateTask(), 0, 10000);
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {

            int numberOfTotalEntryOrExit = SensorConstants.getRandomNumberInRange(-5, 5, sensorState.getTotalPeople());

            if(sensorState.getTotalPeople() + numberOfTotalEntryOrExit >= 0) {
                if(numberOfTotalEntryOrExit > 0) {
                    sensorState.setTotalPeople(sensorState.getTotalPeople() + numberOfTotalEntryOrExit);
                    sensorState.setTotalEntered(numberOfTotalEntryOrExit);
                    sensorState.setTotalExited(0);
                }
                else if(numberOfTotalEntryOrExit < 0) {
                    numberOfTotalEntryOrExit = numberOfTotalEntryOrExit * -1;
                    sensorState.setTotalPeople(sensorState.getTotalPeople() - numberOfTotalEntryOrExit);
                    sensorState.setTotalEntered(0);
                    sensorState.setTotalExited(numberOfTotalEntryOrExit);
                }

                if(sensorState.getTotalPeople() < 0) {
                    sensorState.setTotalPeople(0);
                }
                //notify all observers
                changed();
            }
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        ObjectMapper mapper = new ObjectMapper();
        try {
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
                SecuritySensorRequest requestDto = mapper.readValue(json, SecuritySensorRequest.class);

                String securityPin = requestDto.getPin();

                if(requestDto.isEvacuate() == true) {
                    sensorState.setTotalPeople(0);
                }
                if(securityPin != null && securityPin.equals(SensorConstants.SECURITY_PIN) == false) {
                    sensorState.setHasThief(true);
                }
                changed();
                String responseJson = mapper.writeValueAsString(sensorState);
                exchange.respond(CREATED, responseJson);

            } catch (Exception ex) {

            }
        } else {
            exchange.respond(CREATED);
        }
    }
}
