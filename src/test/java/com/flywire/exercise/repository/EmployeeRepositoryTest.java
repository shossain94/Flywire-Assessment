package com.flywire.exercise.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flywire.exercise.model.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeRepositoryTest {

    private EmployeeRepository employeeRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Path tempFilePath;

    @BeforeEach
    void setUp() throws IOException {
        tempFilePath = Files.createTempFile("runtime-data-test", ".json");
        Path originalDataFilePath = Paths.get("src/main/resources/json/data.json");
        Files.copy(originalDataFilePath, tempFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        employeeRepository = new EmployeeRepository(tempFilePath.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFilePath);
    }

    @Test
    void testGetAllEmployees() throws IOException {
        List<Employee> employees = employeeRepository.getAllEmployees();
        assertNotNull(employees);
        assertFalse(employees.isEmpty(), "The employee list should not be empty.");
    }

    @Test
    void testSaveEmployees() throws IOException {
        System.out.println("Before test starts: " + Files.readString(tempFilePath));

        List<Employee> employees = new ArrayList<>(employeeRepository.getAllEmployees());
        int initialSize = employees.size();

        Employee newEmployee = new Employee();
        newEmployee.setId(initialSize + 1);
        newEmployee.setName("Jimi Walt");
        newEmployee.setPosition("Developer");
        newEmployee.setHireDate("08/30/2024");
        newEmployee.setActive(true);
        newEmployee.setDirectReports(List.of());

        employees.add(newEmployee);

        System.out.println("Before saving, employee list size: " + employees.size());
        System.out.println("Before saving, employee list content: " + employees);

        employeeRepository.saveEmployees(employees);

        System.out.println("After saving: " + Files.readString(tempFilePath));

        List<Employee> updatedEmployees = employeeRepository.getAllEmployees();

        System.out.println("After reloading, employee list size: " + updatedEmployees.size());
        System.out.println("After reloading, employee list content: " + updatedEmployees);

        assertEquals(initialSize + 1, updatedEmployees.size());
        assertTrue(updatedEmployees.stream().anyMatch(e -> e.getName().equals("Jimi Walt")));
    }

    @Test
    void testSaveEmptyEmployeeList() throws IOException {
        List<Employee> employees = List.of();
        employeeRepository.saveEmployees(employees);
        List<Employee> updatedEmployees = employeeRepository.getAllEmployees();
        assertTrue(updatedEmployees.isEmpty());
    }

    @Test
    void testSaveInvalidEmployeeData() {
        Employee invalidEmployee = new Employee();
        List<Employee> employees = List.of(invalidEmployee);
        assertThrows(IllegalArgumentException.class, () -> employeeRepository.saveEmployees(employees));
    }
}
