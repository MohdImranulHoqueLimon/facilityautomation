package frausas.mobilecomputing.facilityautomation.sensorstate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllSensorState {
    LightSensorState lightSensorState;
    SecuritySensorState securitySensorState;
}
