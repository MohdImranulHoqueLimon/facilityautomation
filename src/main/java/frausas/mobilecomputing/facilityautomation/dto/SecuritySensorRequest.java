package frausas.mobilecomputing.facilityautomation.dto;

import lombok.Data;

@Data
public class SecuritySensorRequest {
    boolean evacuate;
    String pin;
}
