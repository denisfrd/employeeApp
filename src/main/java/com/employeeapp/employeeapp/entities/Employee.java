package com.employeeapp.employeeapp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Employee {

@Id
    private Long employeeId;
    private String employeeName;
    private String department;

    public Employee() {}

    //Employee constructor
    public Employee(Long employeeId, String employeeName, String department){
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
    }

    //Getters & Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
