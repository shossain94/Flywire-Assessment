package com.flywire.exercise.service;

import com.flywire.exercise.model.Employee;
import com.flywire.exercise.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEmployeeById() throws IOException {
 
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("John Doe");

        when(employeeRepository.getAllEmployees()).thenReturn(List.of(employee));

        Optional<Employee> foundEmployee = employeeService.getEmployeeById(1);

        assertTrue(foundEmployee.isPresent());
        assertEquals("John Doe", foundEmployee.get().getName());
    }

    @Test
    void testAddEmployee() throws IOException {

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("John Doe");

        when(employeeRepository.getAllEmployees()).thenReturn(List.of());

        employeeService.addEmployee(employee);

        verify(employeeRepository, times(1)).saveEmployees(anyList());
    }

    @Test
void testAddEmployeeThrowsExceptionWhenIdExists() throws IOException {

    Employee employee = new Employee();
    employee.setId(1);
    employee.setName("John Doe");

    when(employeeRepository.getAllEmployees()).thenReturn(List.of(employee));

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        employeeService.addEmployee(employee);
    });

    assertEquals("Employee with ID: 1 already exists.", exception.getMessage());
}


    @Test
    void testGetActiveEmployeesSortedByLastName() throws IOException {

        Employee employee1 = new Employee();
        employee1.setId(1);
        employee1.setName("John Smith");
        employee1.setActive(true);

        Employee employee2 = new Employee();
        employee2.setId(2);
        employee2.setName("Jane Doe");
        employee2.setActive(true);

        Employee inactiveEmployee = new Employee();
        inactiveEmployee.setId(3);
        inactiveEmployee.setName("Inactive Person");
        inactiveEmployee.setActive(false);

        when(employeeRepository.getAllEmployees()).thenReturn(List.of(employee1, employee2, inactiveEmployee));

        List<Employee> activeEmployees = employeeService.getActiveEmployeesSortedByLastName();

        assertEquals(2, activeEmployees.size());
        assertEquals("Jane Doe", activeEmployees.get(0).getName());  // Sorted by last name
        assertEquals("John Smith", activeEmployees.get(1).getName());
    }

    @Test
    void testGetEmployeesHiredInRange() throws IOException {
   
        Employee employee1 = new Employee();
        employee1.setId(1);
        employee1.setName("John Smith");
        employee1.setHireDate("08/30/2021");

        Employee employee2 = new Employee();
        employee2.setId(2);
        employee2.setName("Jane Doe");
        employee2.setHireDate("01/15/2023");

        Employee employee3 = new Employee();
        employee3.setId(3);
        employee3.setName("Test Employee");
        employee3.setHireDate("09/01/2024");

        when(employeeRepository.getAllEmployees()).thenReturn(List.of(employee1, employee2, employee3));

        List<Employee> employeesInRange = employeeService.getEmployeesHiredInRange("01/01/2023", "12/31/2023");

        assertEquals(1, employeesInRange.size());
        assertEquals("Jane Doe", employeesInRange.get(0).getName());
    }

    @Test
    void testDeactivateEmployee() throws IOException {

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("John Doe");
        employee.setActive(true);

        when(employeeRepository.getAllEmployees()).thenReturn(List.of(employee));

        employeeService.deactivateEmployee(1);

        assertFalse(employee.getActive()); 

        verify(employeeRepository, times(1)).saveEmployees(anyList());
    }

    @Test
    void testDeactivateNonExistentEmployee() throws IOException {

        when(employeeRepository.getAllEmployees()).thenReturn(List.of());

        employeeService.deactivateEmployee(1);

        verify(employeeRepository, times(1)).saveEmployees(anyList());
    }
}
