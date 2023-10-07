package com.example.xcelparser2.model;

import java.sql.Date;

public class ShiftPlan {
    private Date shiftStartTime;
    private int employeeId;

    public ShiftPlan(java.util.Date shiftStartTime, int employeeId) {
        this.shiftStartTime = new java.sql.Date(shiftStartTime.getTime());
        this.employeeId = employeeId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public Date getShiftStartTime() {
        return shiftStartTime;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setShiftStartTime(Date shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }
}
