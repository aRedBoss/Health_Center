import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Customer;
import model.Doctor;

public class DoctorTest {
    private Doctor doctor;
    private Customer customer;

    @BeforeEach
    public void setUp() {
        doctor = new Doctor(1);
        customer = new Customer(1, 10);
    }

    @Test
    public void testInitialization() {
        assertEquals(1, doctor.getId());
        assertFalse(doctor.isBusy());
        assertEquals(0, doctor.getTotalBusyTime());
        assertTrue(doctor.getServedCustomers().isEmpty());
    }

    @Test
    public void testServe() {
        doctor.serve(customer, 15);
        assertTrue(doctor.isBusy());
        assertEquals(15, doctor.getTotalBusyTime());
        assertEquals(1, doctor.getServedCustomers().size());
        assertEquals(customer, doctor.getServedCustomers().peek());
    }

    @Test
    public void testFinish() {
        doctor.serve(customer, 15);
        doctor.finish();
        assertFalse(doctor.isBusy());
    }

    @Test
    public void testReset() {
        doctor.serve(customer, 15);
        doctor.reset();
        assertFalse(doctor.isBusy());
        assertEquals(0, doctor.getTotalBusyTime());
        assertTrue(doctor.getServedCustomers().isEmpty());
    }
}
