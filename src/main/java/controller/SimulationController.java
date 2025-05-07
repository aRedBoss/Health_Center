package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;
import model.Doctor;
import database.CustomerDAO;
import database.DoctorDAO;

import java.util.*;
import java.util.concurrent.*;

public class SimulationController {
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colCustomerId;
    @FXML private TableColumn<Customer, String> colArrived;
    @FXML private TableColumn<Customer, String> colWaitTime;
    @FXML private TableColumn<Customer, String> colServiceTime;
    @FXML private TableColumn<Customer, String> colDoctor;
    @FXML private TableColumn<Customer, String> colStatus;
    @FXML private LineChart<String, Number> centerChart;
    @FXML private BarChart<String, Number> doctorChart;
    @FXML private ListView<String> listView;
    @FXML private Label servedLabel;
    @FXML private Label unservedLabel;
    @FXML private Label bestDoctorLabel;
    @FXML private Label worstDoctorLabel;

    @FXML private Spinner<Integer> arrivalMinSpinner;
    @FXML private Spinner<Integer> arrivalMaxSpinner;
    @FXML private Spinner<Integer> serviceMinSpinner;
    @FXML private Spinner<Integer> serviceMaxSpinner;
    @FXML private Spinner<Integer> tickSpeedSpinner;

    private final List<Doctor> doctors = new ArrayList<>();
    private final Queue<Customer> queue = new ConcurrentLinkedQueue<>();
    private final ObservableList<Customer> customerData = FXCollections.observableArrayList();
    private final Random random = new Random();
    private int customerId = 1;
    private int currentMinute = 0;
    private int nextArrivalMinute = 0;
    private ScheduledExecutorService simulationClock;

    public void initialize() {
        setupTable();
        setupSpinners();
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        doctors.addAll(DoctorDAO.getAllDoctors());
        if (doctors.isEmpty()) {
            for (int i = 1; i <= 4; i++) {
                Doctor d = new Doctor(i);
                DoctorDAO.save(d);
                doctors.add(d);
            }
        }
    }

    private void setupSpinners() {
        arrivalMinSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        arrivalMaxSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));
        serviceMinSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 4));
        serviceMaxSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 8));
        tickSpeedSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 2000, 500, 100));
    }

    private void setupTable() {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colArrived.setCellValueFactory(new PropertyValueFactory<>("arrivalTimeFormatted"));
        colWaitTime.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getWaitTime() + " min"));
        colServiceTime.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getServiceTime() + " min"));
        colDoctor.setCellValueFactory(cellData -> {
            int docId = cellData.getValue().getAssignedDoctor();
            return new SimpleStringProperty(docId == -1 ? "—" : "Doctor " + docId);
        });
        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

        colWaitTime.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    double waitTime = Double.parseDouble(item.split(" ")[0]);
                    setStyle(waitTime > 5
                            ? "-fx-background-color: #E57373; -fx-text-fill: white;"
                            : "-fx-background-color: #81C784; -fx-text-fill: black;");
                }
            }
        });

        customerTable.setItems(customerData);
    }

    @FXML
    public void startSimulation() {
        if (simulationClock != null && !simulationClock.isShutdown()) return;

        // Reset state
        currentMinute = 0;
        nextArrivalMinute = 0;
        customerId = 1;
        queue.clear();
        customerData.clear();
        listView.getItems().clear();
        servedLabel.setText("Served Customers: 0");
        unservedLabel.setText("Unserved Customers: 0");
        bestDoctorLabel.setText("Best Doctor: —");
        worstDoctorLabel.setText("Worst Doctor: —");
        centerChart.getData().clear();
        doctorChart.getData().clear();
        for (Doctor d : doctors) d.reset(); // You must implement this in Doctor class

        // Read inputs
        int arrivalMin = arrivalMinSpinner.getValue();
        int arrivalMax = arrivalMaxSpinner.getValue();
        int serviceMin = serviceMinSpinner.getValue();
        int serviceMax = serviceMaxSpinner.getValue();
        int tickSpeed = tickSpeedSpinner.getValue();

        simulationClock = Executors.newSingleThreadScheduledExecutor();

        simulationClock.scheduleAtFixedRate(() -> {
            currentMinute++;

            if (currentMinute < 80 && currentMinute >= nextArrivalMinute) {
                Customer c = new Customer(customerId++, currentMinute);
                queue.add(c);
                Platform.runLater(() -> customerData.add(c));
                nextArrivalMinute = currentMinute + random.nextInt(arrivalMax - arrivalMin + 1) + arrivalMin;
            }

            for (Doctor doc : doctors) {
                if (!doc.isBusy()) {
                    Customer c = queue.peek();
                    if (c != null && !c.isBeingServed()) {
                        queue.poll();
                        c.setBeingServed(true);
                        c.startService(currentMinute, doc.getId());

                        int serviceDuration = random.nextInt(serviceMax - serviceMin + 1) + serviceMin;
                        doc.serve(c, serviceDuration);

                        Platform.runLater(() -> {
                            listView.getItems().add("Customer " + c.getId() + " served by Doctor " + doc.getId());
                            customerTable.refresh();
                        });

                        int endMinute = currentMinute + serviceDuration;
                        simulationClock.schedule(() -> {
                            c.endService(endMinute);
                            doc.finish();
                            CustomerDAO.save(c);

                            Platform.runLater(() -> {
                                customerTable.refresh();
                                updateDoctorChart();
                            });
                        }, serviceDuration, TimeUnit.SECONDS);
                    }
                }
            }

            if (currentMinute >= 96) {
                simulationClock.shutdown();

                for (Customer c : queue) {
                    if (!c.isBeingServed() && c.getServiceStartTime() == -1) {
                        c.markUnserved();
                    }
                }

                Platform.runLater(() -> {
                    customerTable.refresh();
                    updateHealthCenterChart();
                });
            }

        }, 0, tickSpeed, TimeUnit.MILLISECONDS);
    }

    private void updateDoctorChart() {
        Platform.runLater(() -> {
            doctorChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();

            for (Doctor d : doctors) {
                double avgTime = d.getServedCustomers().isEmpty() ? 0 :
                        d.getTotalBusyTime() / d.getServedCustomers().size();
                series.getData().add(new XYChart.Data<>("Doctor " + d.getId(), avgTime));
            }

            doctorChart.getData().add(series);
        });
    }

    private void updateHealthCenterChart() {
        Platform.runLater(() -> {
            centerChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();

            double totalWaitTime = 0;
            double totalServiceTime = 0;
            int served = 0;
            int unserved = 0;

            for (Customer c : customerData) {
                if (c.isMarkedUnserved()) {
                    unserved++;
                } else if (c.getServiceEndTime() != -1) {
                    served++;
                    totalWaitTime += c.getWaitTime();
                    totalServiceTime += c.getServiceTime();
                }
            }

            double avgWaitTime = served == 0 ? 0 : totalWaitTime / served;
            double avgServiceTime = served == 0 ? 0 : totalServiceTime / served;

            series.getData().add(new XYChart.Data<>("Avg. Wait Time", avgWaitTime));
            series.getData().add(new XYChart.Data<>("Avg. Service Time", avgServiceTime));
            centerChart.getData().add(series);

            servedLabel.setText("Served Customers: " + served);
            unservedLabel.setText("Unserved Customers: " + unserved);

            Doctor best = null, worst = null;
            double bestTime = Double.MAX_VALUE, worstTime = -1;

            for (Doctor d : doctors) {
                int count = d.getServedCustomers().size();
                if (count == 0) continue;
                double avg = d.getTotalBusyTime() / count;

                if (avg < bestTime) {
                    bestTime = avg;
                    best = d;
                }
                if (avg > worstTime) {
                    worstTime = avg;
                    worst = d;
                }
            }

            bestDoctorLabel.setText("Best Doctor: " + (best != null ? "Doctor " + best.getId() : "—"));
            worstDoctorLabel.setText("Worst Doctor: " + (worst != null ? "Doctor " + worst.getId() : "—"));

        });
    }
}