package frausas.mobilecomputing.facilityautomation.dto;

import lombok.Data;

@Data
public class SensorStateDto {
    String sensorName;
    int runningPort;
    boolean isRunning;
    int taskDuration; //Duration in second
}
