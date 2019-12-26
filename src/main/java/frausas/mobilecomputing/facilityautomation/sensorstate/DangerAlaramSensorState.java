package frausas.mobilecomputing.facilityautomation.sensorstate;

import lombok.Data;

@Data
public class DangerAlaramSensorState {
    boolean on;

    public DangerAlaramSensorState(){
        on = true;
    }
}
