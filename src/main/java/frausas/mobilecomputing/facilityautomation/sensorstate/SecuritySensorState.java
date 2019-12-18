package frausas.mobilecomputing.facilityautomation.sensorstate;

import lombok.Data;

@Data
public class SecuritySensorState {

    int totalEntered;
    int totalExited;
    int totalPeople;
    boolean hasThief;

    public SecuritySensorState() {
        totalPeople = 0;
        totalEntered = 0;
        totalExited = 0;
        hasThief = false;
    }
}
