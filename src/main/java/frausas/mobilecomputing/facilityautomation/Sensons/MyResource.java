package frausas.mobilecomputing.facilityautomation.Sensons;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;

import java.util.Timer;
import java.util.TimerTask;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;

public class MyResource extends ConcurrentCoapResource {

    public MyResource(String name) {
        super(name, SINGLE_THREADED);

        setObservable(true); // enable observing
        setObserveType(CoAP.Type.CON); // configure the notification type to CONs
        getAttributes().setObservable(); // mark observable in the Link-Format

        // schedule a periodic update task, otherwise let events call changed()
        Timer timer = new Timer();
        timer.schedule(new UpdateTask(), 0, 2000);
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            // .. periodic update of the resource
            changed(); // notify all observers
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.respond("we are best friend");
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

/*    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        CoAP.ResponseCode response;
        synchronized (this) {
            // critical section
            response = CoAP.ResponseCode.CHANGED;
        }
        exchange.respond(response);
    }*/

    /*public void mainFunction() {
        CoapServer server = new CoapServer();
        server.add(new MyResource("example"));
        server.add(new CoapResource("target") {
            @Override
            public void handleGET(CoapExchange exchange) {
                exchange.respond("Target payload coap response limon");
            }
        });
        server.start();
        CoapClient client = new CoapClient("coap://localhost:5683/example");
        System.out.println(client.get().getResponseText());
    }*/

    /*public MyResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.accept();

        CoapClient client = createClient("localhost:5683/target");
        client.get(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                exchange.respond(response.getCode(), response.getPayload());
            }

            @Override
            public void onError() {
                exchange.respond(CoAP.ResponseCode.BAD_GATEWAY);
            }
        });
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        CoAP.ResponseCode response;
        synchronized (this) {
            // critical section
            response = CoAP.ResponseCode.CHANGED;
        }
        exchange.respond(response);
    }*/
}
