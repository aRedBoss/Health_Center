package model;

import java.util.LinkedList;
import java.util.Queue;

public class Doctor {
    private final int id;
    private boolean isBusy;
    private double totalBusyTime;
    private final Queue<Customer> served;

    public Doctor(int id) {
        this.id = id;
        this.served = new LinkedList<>();
        this.isBusy = false;
        this.totalBusyTime = 0;
    }

    public void serve(Customer c, double time) {
        isBusy = true;
        served.add(c);
        totalBusyTime += time;
    }

    public void finish() {
        isBusy = false;
    }

    public boolean isBusy() { return isBusy; }
    public double getTotalBusyTime() { return totalBusyTime; }
    public Queue<Customer> getServedCustomers() { return served; }
    public int getId() { return id; }
}