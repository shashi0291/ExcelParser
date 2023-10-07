package com.example.xcelparser2.database;

import com.example.xcelparser2.model.ShiftPlan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ShiftPlanDAO {

    String insertSortPlanQuery = "INSERT INTO sort_plan_table (sortStartTime, employeeId) VALUES (?, ?);";

    String getSortTimeToEmpIdMappingQuery = "SELECT sortStartTime, GROUP_CONCAT(employeeId) AS EmployeeList\n" +
            "FROM sort_plan_table\n" +
            "GROUP BY sortStartTime\n" +
            "ORDER BY sortStartTime ASC;";

    PreparedStatement preparedStatement = null;

    public void insertSortPlan(Connection dbConnection, ShiftPlan shiftPlan) {
        try {
            preparedStatement = dbConnection.prepareStatement(insertSortPlanQuery);
            preparedStatement.setDate(1, shiftPlan.getShiftStartTime());
            preparedStatement.setInt(2, shiftPlan.getEmployeeId());
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data inserted successfully.");
            } else {
                System.out.println("Insertion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void querySortPlan() {

    }
}
