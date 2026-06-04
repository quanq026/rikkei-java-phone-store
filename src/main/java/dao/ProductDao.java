package dao;

import model.Product;
import java.sql.*;

public class ProductDao {
    public boolean add(Product p) throws SQLException {
        String sql = "CALL add_product(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, p.getName());
            stmt.setString(2, p.getBrand());
            stmt.setDouble(3, p.getPrice());
            stmt.setInt(4, p.getStock());
            stmt.execute();
            return true;
        }
    }
}
