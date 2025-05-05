# Project Documentation

## Architecture Overview
The project is structured into several packages:
- **controller**: Contains the main simulation controller.
- **database**: Contains classes for database connection and data access objects (DAOs).
- **model**: Contains the data models for Customer and Doctor.

## Class Descriptions

### Customer
Represents a customer in the health center. Tracks arrival time, service start time, service end time, assigned doctor, and status.

### Doctor
Represents a doctor in the health center. Tracks the doctor's ID, busy status, total busy time, and served customers.

### SimulationController
Controls the simulation logic, including customer arrival, doctor assignment, and updating the user interface.

### Database
Provides a connection to the MariaDB database.

### CustomerDAO
Data access object for saving customer data to the database.

### DoctorDAO
Data access object for saving and retrieving doctor data from the database.

## Simulation Logic
The simulation is controlled by the `SimulationController` class. The main steps are:
1. **Initialization**: Set up the table and load doctors from the database.
2. **Start Simulation**: Begin the simulation clock and handle customer arrivals and doctor assignments.
3. **Customer Arrival**: Add customers to the queue upon arrival.
4. **Doctor Assignment**: Assign available doctors to customers and track service times.
5. **Update Charts**: Update the charts to display performance metrics.

## Database Integration
The project uses MariaDB to store customer and doctor data. The `Database` class provides a connection to the database, and the `CustomerDAO` and `DoctorDAO` classes handle data access.

### Database Schema

#### Customers Table
- `id`: Integer, primary key
- `arrival`: Integer, arrival time
- `service_start`: Integer, service start time
- `service_end`: Integer, service end time
- `doctor_id`: Integer, assigned doctor ID

#### Doctors Table
- `id`: Integer, primary key
## Simulation Test Results

To evaluate the performance of the health center simulation, a test scenario was conducted using 4 doctors. The simulation tracked average service times per doctor and overall system performance.

### Scenario: 4 Doctors (Default Configuration)
- **Observation**: The system handled customer flow efficiently, though some doctors had significantly higher average service times than others.
- **Average Service Times**:

    - Doctor 1: ~6.8 minutes
    - Doctor 2: ~5.0 minutes
    - Doctor 3: ~4.8 minutes
    - Doctor 4: ~6.5 minutes

- **Conclusion**: While the system maintained a good balance overall, the variation in doctor performance suggests potential for optimization in workload distribution.





