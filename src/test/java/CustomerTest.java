import static org.junit.jupiter.api.Assertions.*;

import model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomerTest {
    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer(1, 10);
    }

    @Test
    public void testInitialization() {
        assertEquals(1, customer.getId());
        assertEquals(10, customer.getArrivalTime());
        assertEquals(-1, customer.getServiceStartTime());
        assertEquals(-1, customer.getServiceEndTime());
        assertEquals(-1, customer.getAssignedDoctor());
        assertFalse(customer.isBeingServed());
        assertFalse(customer.isMarkedUnserved());
    }

    @Test
    public void testStartService() {
        customer.startService(20, 2);
        assertEquals(20, customer.getServiceStartTime());
        assertEquals(2, customer.getAssignedDoctor());
        assertTrue(customer.isBeingServed());
    }

    @Test
    public void testEndService() {
        customer.startService(20, 2);
        customer.endService(30);
        assertEquals(30, customer.getServiceEndTime());
        assertFalse(customer.isBeingServed());
    }

    @Test
    public void testMarkUnserved() {
        customer.markUnserved();
        assertTrue(customer.isMarkedUnserved());
        assertFalse(customer.isBeingServed());
    }

    @Test
    public void testGetWaitTime() {
        assertEquals(0, customer.getWaitTime());
        customer.startService(20, 2);
        assertEquals(10, customer.getWaitTime());
    }

    @Test
    public void testGetServiceTime() {
        assertEquals(0, customer.getServiceTime());
        customer.startService(20, 2);
        customer.endService(30);
        assertEquals(10, customer.getServiceTime());
    }
}

