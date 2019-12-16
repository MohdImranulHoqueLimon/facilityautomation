package frausas.mobilecomputing.facilityautomation.Sensons;

public abstract class AbstractSensor {
    abstract void doAction();
    abstract void stopAction();
    abstract void getState();
}
