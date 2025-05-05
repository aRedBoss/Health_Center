package database;

import model.Customer;
import java.sql.*;

public class CustomerDAO {
    public static void save(Customer c) {
        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO customers (arrival, service_start, service_end, doctor_id) VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, c.getArrivalTime());
            ps.setInt(2, c.getServiceStartTime());
            ps.setInt(3, c.getServiceEndTime());
            ps.setInt(4, c.getAssignedDoctor());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
