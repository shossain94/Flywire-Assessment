package com.flywire.exercise.repository;

import java.io.IOException;
import java.util.List;
import java.io.File;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flywire.exercise.model.Employee;

@Repository
public class EmployeeRepository {

    private final String dataFilePath;
    private final ObjectMapper objectMapper;

     public EmployeeRepository() throws IOException {
        this.dataFilePath = new ClassPathResource("json/data.json").getFile().getAbsolutePath();
        this.objectMapper = new ObjectMapper();
    }

    public List<Employee> getAllEmployees() throws IOException {
        return List.of(objectMapper.readValue(new File(dataFilePath), Employee[].class));
    }

    public void saveEmployees(List<Employee> employees) throws IOException {
        objectMapper.writeValue(new File(dataFilePath), employees);
    }
}
