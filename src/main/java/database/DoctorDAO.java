package database;

import model.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    public static void save(Doctor d) {
        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO doctors (id) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, d.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        try (Connection conn = Database.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM doctors");
            while (rs.next()) {
                doctors.add(new Doctor(rs.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }
}

