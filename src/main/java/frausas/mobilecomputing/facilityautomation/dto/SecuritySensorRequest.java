package frausas.mobilecomputing.facilityautomation.dto;

import lombok.Data;

@Data
public class SecuritySensorRequest {
    int peopleExited;
    int peopleEntered;
    String securityPin;
}
