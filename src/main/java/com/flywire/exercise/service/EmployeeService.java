package com.flywire.exercise.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flywire.exercise.model.Employee;
import com.flywire.exercise.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getActiveEmployeesSortedByLastName() throws IOException {
        return employeeRepository.getAllEmployees().stream()
                .filter(Employee::getActive)
                .sorted((e1, e2) -> {
                    String lastName1 = e1.getName().split(" ")[1];
                    String lastName2 = e2.getName().split(" ")[1];
                    return lastName1.compareToIgnoreCase(lastName2);
                })
                .collect(Collectors.toList());
    }

    public Optional<Employee> getEmployeeById(int id) throws IOException {
        return employeeRepository.getAllEmployees().stream()
                .filter(employee -> employee.getId() == id)
                .findFirst();
    }

    public List<Employee> getEmployeesHiredInRange(String startDate, String endDate) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        return employeeRepository.getAllEmployees().stream()
                .filter(employee -> {
                    LocalDate hireDate = LocalDate.parse(employee.getHireDate(), formatter);
                    return (hireDate.isEqual(start) || hireDate.isAfter(start)) &&
                            (hireDate.isEqual(end) || hireDate.isBefore(end));
                })
                .sorted((e1, e2) -> LocalDate.parse(e2.getHireDate(), formatter)
                        .compareTo(LocalDate.parse(e1.getHireDate(), formatter)))
                .collect(Collectors.toList());
    }

    public void addEmployee(Employee employee) throws IOException {
        List<Employee> employees = new ArrayList<>(employeeRepository.getAllEmployees());

        boolean idExists = employees.stream().anyMatch(e -> e.getId() == employee.getId());
        if (idExists) {
            throw new IllegalArgumentException("Employee with ID: " + employee.getId() + " already exists.");
        }

        employees.add(employee);
        employeeRepository.saveEmployees(employees);
    }

    public void deactivateEmployee(int id) throws IOException {
        List<Employee> employees = employeeRepository.getAllEmployees();
        employees.forEach(employee -> {
            if (employee.getId() == id) {
                employee.setActive(false);
            }
        });
        employeeRepository.saveEmployees(employees);
    }

    public Employee getEmployeeWithDirectReports(int id) throws IOException {
        Optional<Employee> employeeOpt = getEmployeeById(id);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            List<Employee> directReports = getDirectHires(employee.getDirectReports());
            String directReportNames = directReports.stream()
                    .map(Employee::getName)
                    .collect(Collectors.joining(", "));
            employee.setDirectReportNames(directReportNames);
            return employee;
        } else {
            return null;
        }
    }

    private List<Employee> getDirectHires(List<Integer> directReportIds) throws IOException {
        List<Employee> allEmployees = employeeRepository.getAllEmployees();
        return allEmployees.stream()
                .filter(emp -> directReportIds.contains(emp.getId()))
                .collect(Collectors.toList());
    }
}
