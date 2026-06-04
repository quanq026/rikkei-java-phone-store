package dao;

import model.Customer;
import java.sql.*;

public class CustomerDao {
    public boolean add(Customer c) throws SQLException {
        String sql = "CALL add_customer(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, c.getName());
            stmt.setString(2, c.getPhone());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getAddress());
            stmt.execute();
            return true;
        }
    }
}
