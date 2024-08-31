package com.flywire.exercise.controller;

import com.flywire.exercise.model.Employee;
import com.flywire.exercise.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    //http request endpoint that returns a list of all active employees in alphabetical order of last name
    @GetMapping("/active")
    public ResponseEntity<List<Employee>> getAllActiveEmployees() {
        try {
            List<Employee> employees = employeeService.getActiveEmployeesSortedByLastName();
            return ResponseEntity.ok(employees);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    //http request endpoint that takes in an ID and returns a JSON response of the matching employees, as well as the names of their direct hires
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable int id) {
        try {
            Employee employee = employeeService.getEmployeeWithDirectReports(id);
            if (employee != null) {
                return ResponseEntity.ok(employee);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    //http request endpoint that takes a date range, and returns a JSON response of all employees hired in that date range. Sort by descending order of date hired
    @GetMapping("/hired")
    public ResponseEntity<List<Employee>> getEmployeesHiredInRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            List<Employee> employees = employeeService.getEmployeesHiredInRange(startDate, endDate);
            return ResponseEntity.ok(employees);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    //http request endpoint that takes a name, id, position, direct reports, and manager to creates a new employee. The employee should be added to the JSON file
    @PostMapping("/add")
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) {
        try {
            Optional<Employee> existingEmployee = employeeService.getEmployeeById(employee.getId());
            if (existingEmployee.isPresent()) {
                return ResponseEntity.badRequest().body("Employee with ID already exists.");
            }

            employeeService.addEmployee(employee);
            return ResponseEntity.ok("Employee added successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error adding employee.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //http request endpoint that takes in an ID and deactivates an employee
    @PostMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateEmployee(@PathVariable int id) {
        try {
            Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
            if (employeeOpt.isPresent()) {
                employeeService.deactivateEmployee(id);
                return ResponseEntity.ok("Employee deactivated successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error deactivating employee.");
        }
    }
}
