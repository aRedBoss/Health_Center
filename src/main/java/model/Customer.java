package model;

import javafx.beans.property.*;

import java.util.Objects;

public class Customer {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty arrivalTime = new SimpleIntegerProperty();
    private final IntegerProperty serviceStartTime = new SimpleIntegerProperty(-1);
    private final IntegerProperty serviceEndTime = new SimpleIntegerProperty(-1);
    private final IntegerProperty assignedDoctor = new SimpleIntegerProperty(-1);
    private final BooleanProperty beingServed = new SimpleBooleanProperty(false);
    private final BooleanProperty markedUnserved = new SimpleBooleanProperty(false); // NEW

    public Customer(int id, int arrivalTime) {
        this.id.set(id);
        this.arrivalTime.set(arrivalTime);
    }

    public void startService(int currentTime, int doctorId) {
        this.serviceStartTime.set(currentTime);
        this.assignedDoctor.set(doctorId);
        this.beingServed.set(true);
    }

    public void endService(int currentTime) {
        this.serviceEndTime.set(currentTime);
        this.beingServed.set(false);
    }

    public void markUnserved() {
        this.markedUnserved.set(true);
        this.beingServed.set(false);
    }

    public int getWaitTime() {
        return serviceStartTime.get() == -1 ? 0 : serviceStartTime.get() - arrivalTime.get();
    }

    public int getServiceTime() {
        return (serviceStartTime.get() != -1 && serviceEndTime.get() != -1)
                ? serviceEndTime.get() - serviceStartTime.get()
                : 0;
    }

    public String getArrivalTimeFormatted() {
        return "Min " + arrivalTime.get();
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public int getArrivalTime() { return arrivalTime.get(); }
    public IntegerProperty arrivalTimeProperty() { return arrivalTime; }

    public int getServiceStartTime() { return serviceStartTime.get(); }
    public IntegerProperty serviceStartTimeProperty() { return serviceStartTime; }

    public int getServiceEndTime() { return serviceEndTime.get(); }
    public IntegerProperty serviceEndTimeProperty() { return serviceEndTime; }

    public int getAssignedDoctor() { return assignedDoctor.get(); }
    public IntegerProperty assignedDoctorProperty() { return assignedDoctor; }

    public boolean isBeingServed() { return beingServed.get(); }
    public void setBeingServed(boolean value) { beingServed.set(value); }
    public BooleanProperty beingServedProperty() { return beingServed; }

    public boolean isMarkedUnserved() { return markedUnserved.get(); }
    public BooleanProperty markedUnservedProperty() { return markedUnserved; }

    public String getStatus() {
        if (markedUnserved.get()) return "Unserved";
        if (serviceEndTime.get() != -1) return "Served";
        if (beingServed.get()) return "Being Served";
        if (serviceStartTime.get() == -1) return "Waiting";
        return "Unknown";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return getId() == customer.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
