package frausas.mobilecomputing.facilityautomation;

import frausas.mobilecomputing.facilityautomation.Sensons.MyResource;
import org.eclipse.californium.core.CoapServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FacilityautomationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacilityautomationApplication.class, args);

        CoapServer server = new CoapServer(8085);
        server.add(new MyResource("example"));
        server.start();
    }

}
