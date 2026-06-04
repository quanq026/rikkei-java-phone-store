package dao;

import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    public List<Product> getAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        }
        return list;
    }

    public Product getById(int id) throws SQLException {
        String sql = "SELECT * FROM product WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        }
        return null;
    }

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

    public boolean update(Product p) throws SQLException {
        String sql = "UPDATE product SET name = ?, brand = ?, price = ?, stock = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getName());
            stmt.setString(2, p.getBrand());
            stmt.setDouble(3, p.getPrice());
            stmt.setInt(4, p.getStock());
            stmt.setInt(5, p.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM product WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Product> searchByBrand(String brand) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE brand ILIKE ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + brand + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProduct(rs));
                }
            }
        }
        return list;
    }

    public List<Product> searchByPriceRange(double min, double max) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE price >= ? AND price <= ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, min);
            stmt.setDouble(2, max);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProduct(rs));
                }
            }
        }
        return list;
    }

    public List<Product> searchByNameAndStock(String nameKeyword) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE name ILIKE ? AND stock > 0 ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nameKeyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProduct(rs));
                }
            }
        }
        return list;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setBrand(rs.getString("brand"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));
        return p;
    }
}
