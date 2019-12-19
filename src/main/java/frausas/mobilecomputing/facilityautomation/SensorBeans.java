package frausas.mobilecomputing.facilityautomation;

import frausas.mobilecomputing.facilityautomation.sensorstate.AllSensorState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensorBeans {

    @Bean
    public AllSensorState getAllSensorState() {
        AllSensorState allSensorState = new AllSensorState();
        return allSensorState;
    }

}