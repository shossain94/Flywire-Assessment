package com.flywire.exercise.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flywire.exercise.model.Employee;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Repository
public class EmployeeRepository {

    private final Path runtimeDataFilePath;
    private final Path originalDataFilePath;
    private final ObjectMapper objectMapper;

    public EmployeeRepository() throws IOException {
        this.runtimeDataFilePath = Paths.get("runtime-data.json").toAbsolutePath();
        this.originalDataFilePath = Paths.get(new File("src/main/resources/json/data.json").getAbsolutePath());
        this.objectMapper = new ObjectMapper();

        if (Files.notExists(runtimeDataFilePath)) {
            Files.copy(originalDataFilePath, runtimeDataFilePath);
        }
    }

    public EmployeeRepository(String customDataFilePath) throws IOException {
        this.runtimeDataFilePath = Paths.get(customDataFilePath).toAbsolutePath();
        this.originalDataFilePath = null;  
        this.objectMapper = new ObjectMapper();

        
        if (Files.notExists(this.runtimeDataFilePath)) {
            Files.createFile(this.runtimeDataFilePath);
        }
    }

    public List<Employee> getAllEmployees() throws IOException {
        Employee[] employees = objectMapper.readValue(runtimeDataFilePath.toFile(), Employee[].class);
        return Arrays.asList(employees);
    }

    public void saveEmployees(List<Employee> employees) throws IOException {
        validateEmployees(employees); 
        objectMapper.writeValue(runtimeDataFilePath.toFile(), employees);
        if (originalDataFilePath != null) {
            syncToOriginalDataFile();
        }
    }

    private void validateEmployees(List<Employee> employees) {
        for (Employee employee : employees) {
            if (employee.getId() <= 0 || employee.getName() == null || employee.getName().isEmpty()) {
                throw new IllegalArgumentException("Invalid employee data: " + employee);
                }
            }
        }

    private void syncToOriginalDataFile() throws IOException {
        if (originalDataFilePath != null) {
            Files.copy(runtimeDataFilePath, originalDataFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
