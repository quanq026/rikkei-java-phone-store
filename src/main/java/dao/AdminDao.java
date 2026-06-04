package dao;

import model.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDao {

    public AdminDao() {
        ensureDefaultAdmin();
    }

    public boolean login(String username, String password) {
        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra đăng nhập: " + e.getMessage());
        }
        return false;
    }

    public String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            sb.append((char) (password.charAt(i) + 5));
        }
        return sb.toString();
    }

    private void ensureDefaultAdmin() {
        String countSql = "SELECT COUNT(*) FROM admin";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement countStmt = conn.prepareStatement(countSql);
             ResultSet rs = countStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO admin (username, password) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, hashPassword("admin123"));
                    insertStmt.executeUpdate();
                    System.out.println("[DB] Đã khởi tạo tài khoản admin mặc định (admin/admin123)");
                }
            }
        } catch (SQLException e) {
            // Ignore if table does not exist
        }
    }
}
