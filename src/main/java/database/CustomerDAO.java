package database;

import model.Customer;
import java.sql.*;

public class CustomerDAO {
    public static void save(Customer c) {
        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO customers (arrival, service_start, service_end, doctor_id) VALUES (?, ?, ?, ?)"
            );

            // Convert minutes to TIME strings
            String arrivalTime = toSqlTime(c.getArrivalTime());
            String serviceStartTime = c.getServiceStartTime() == -1 ? null : toSqlTime(c.getServiceStartTime());
            String serviceEndTime = c.getServiceEndTime() == -1 ? null : toSqlTime(c.getServiceEndTime());

            ps.setString(1, arrivalTime);
            if (serviceStartTime != null) ps.setString(2, serviceStartTime); else ps.setNull(2, Types.TIME);
            if (serviceEndTime != null) ps.setString(3, serviceEndTime); else ps.setNull(3, Types.TIME);
            ps.setInt(4, c.getAssignedDoctor());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static String toSqlTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d:00", hours, mins);
    }
}
