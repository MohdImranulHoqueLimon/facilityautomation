package frausas.mobilecomputing.facilityautomation.Sensons;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;

public class SecurityAlarmSensor extends ConcurrentCoapResource {

    public SecurityAlarmSensor(String name) {
        super(name, SINGLE_THREADED);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        if (exchange.getRequestOptions().isContentFormat(MediaTypeRegistry.APPLICATION_JSON)) {
            String json = exchange.getRequestText();
            exchange.respond(CREATED, json.toUpperCase());
        } else {
            exchange.respond(CREATED);
        }
    }
}
