package dao;

import model.Invoice;
import model.InvoiceDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceDao {

    public List<Invoice> getAll() throws SQLException {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, c.name AS customer_name FROM invoice i " +
                     "LEFT JOIN customer c ON i.customer_id = c.id " +
                     "ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapInvoice(rs));
            }
        }
        return list;
    }

    public List<InvoiceDetail> getDetailsForInvoice(int invoiceId) throws SQLException {
        List<InvoiceDetail> list = new ArrayList<>();
        String sql = "SELECT d.*, p.name AS product_name FROM invoice_details d " +
                     "LEFT JOIN product p ON d.product_id = p.id " +
                     "WHERE d.invoice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    InvoiceDetail detail = new InvoiceDetail();
                    detail.setId(rs.getInt("id"));
                    detail.setInvoiceId(rs.getInt("invoice_id"));
                    detail.setProductId(rs.getInt("product_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getDouble("unit_price"));
                    detail.setProductName(rs.getString("product_name"));
                    list.add(detail);
                }
            }
        }
        return list;
    }

    public boolean add(Invoice invoice) throws SQLException {
        Connection conn = null;
        PreparedStatement insertInvoice = null;
        PreparedStatement insertDetail = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            String sqlInvoice = "INSERT INTO invoice (customer_id, total_amount) VALUES (?, ?)";
            insertInvoice = conn.prepareStatement(sqlInvoice, Statement.RETURN_GENERATED_KEYS);
            insertInvoice.setInt(1, invoice.getCustomerId());
            insertInvoice.setDouble(2, invoice.getTotalAmount());
            insertInvoice.executeUpdate();

            int invoiceId = 0;
            try (ResultSet generatedKeys = insertInvoice.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    invoiceId = generatedKeys.getInt(1);
                    invoice.setId(invoiceId);
                } else {
                    throw new SQLException("Thêm hóa đơn thất bại, không lấy được ID tự động sinh.");
                }
            }

            // Inventory check and updates are handled by database triggers
            String sqlDetail = "INSERT INTO invoice_details (invoice_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            insertDetail = conn.prepareStatement(sqlDetail);

            for (InvoiceDetail d : invoice.getDetails()) {
                insertDetail.setInt(1, invoiceId);
                insertDetail.setInt(2, d.getProductId());
                insertDetail.setInt(3, d.getQuantity());
                insertDetail.setDouble(4, d.getUnitPrice());
                insertDetail.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (insertInvoice != null) insertInvoice.close();
            if (insertDetail != null) insertDetail.close();
            if (conn != null) conn.close();
        }
    }

    public List<Invoice> searchByCustomerName(String customerName) throws SQLException {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, c.name AS customer_name FROM invoice i " +
                     "INNER JOIN customer c ON i.customer_id = c.id " +
                     "WHERE c.name ILIKE ? " +
                     "ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + customerName + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapInvoice(rs));
                }
            }
        }
        return list;
    }

    public List<Invoice> searchByDate(String dateKeyword) throws SQLException {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, c.name AS customer_name FROM invoice i " +
                     "LEFT JOIN customer c ON i.customer_id = c.id " +
                     "ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Invoice invoice = mapInvoice(rs);
                Timestamp ts = invoice.getCreatedAt();
                if (ts != null) {
                    String tsStr = ts.toString();
                    String yyyy_mm_dd = tsStr.substring(0, 10);
                    String[] parts = yyyy_mm_dd.split("-");
                    String dd_mm_yyyy = parts[2] + "/" + parts[1] + "/" + parts[0];
                    if (yyyy_mm_dd.contains(dateKeyword) || dd_mm_yyyy.contains(dateKeyword)) {
                        list.add(invoice);
                    }
                }
            }
        }
        return list;
    }

    // Recalculate and update total amount for all invoices
    public boolean updateAllInvoiceTotals() throws SQLException {
        String sqlUpdateWithDetails = "UPDATE invoice i " +
                     "SET total_amount = (SELECT SUM(quantity * unit_price) FROM invoice_details WHERE invoice_id = i.id) " +
                     "WHERE EXISTS (SELECT 1 FROM invoice_details WHERE invoice_id = i.id)";
        String sqlUpdateNoDetails = "UPDATE invoice i " +
                     "SET total_amount = 0 " +
                     "WHERE NOT EXISTS (SELECT 1 FROM invoice_details WHERE invoice_id = i.id)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(sqlUpdateWithDetails);
             PreparedStatement stmt2 = conn.prepareStatement(sqlUpdateNoDetails)) {
            boolean r1 = stmt1.executeUpdate() >= 0;
            boolean r2 = stmt2.executeUpdate() >= 0;
            return r1 || r2;
        }
    }

    // Delete invoices with total amount below the specified threshold
    public int deleteLowValueInvoices(double threshold) throws SQLException {
        String sql = "DELETE FROM invoice WHERE total_amount < ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, threshold);
            return stmt.executeUpdate();
        }
    }

    // Retrieve revenue statistics grouped by day/month/year
    public Map<String, Double> getRevenueStats(String type) throws SQLException {
        Map<String, Double> stats = new HashMap<>();
        String sql;
        if ("month".equalsIgnoreCase(type)) {
            sql = "SELECT EXTRACT(YEAR FROM created_at) AS yr, EXTRACT(MONTH FROM created_at) AS mon, SUM(total_amount) AS total_revenue " +
                  "FROM invoice GROUP BY EXTRACT(YEAR FROM created_at), EXTRACT(MONTH FROM created_at) " +
                  "ORDER BY yr DESC, mon DESC";
        } else if ("year".equalsIgnoreCase(type)) {
            sql = "SELECT EXTRACT(YEAR FROM created_at) AS yr, SUM(total_amount) AS total_revenue " +
                  "FROM invoice GROUP BY EXTRACT(YEAR FROM created_at) " +
                  "ORDER BY yr DESC";
        } else {
            sql = "SELECT EXTRACT(YEAR FROM created_at) AS yr, EXTRACT(MONTH FROM created_at) AS mon, EXTRACT(DAY FROM created_at) AS dy, SUM(total_amount) AS total_revenue " +
                  "FROM invoice GROUP BY EXTRACT(YEAR FROM created_at), EXTRACT(MONTH FROM created_at), EXTRACT(DAY FROM created_at) " +
                  "ORDER BY yr DESC, mon DESC, dy DESC";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int yr = (int) rs.getDouble("yr");
                if ("month".equalsIgnoreCase(type)) {
                    int mon = (int) rs.getDouble("mon");
                    stats.put(String.format("%d-%02d", yr, mon), rs.getDouble("total_revenue"));
                } else if ("year".equalsIgnoreCase(type)) {
                    stats.put(String.format("%d", yr), rs.getDouble("total_revenue"));
                } else {
                    int mon = (int) rs.getDouble("mon");
                    int dy = (int) rs.getDouble("dy");
                    stats.put(String.format("%d-%02d-%02d", yr, mon, dy), rs.getDouble("total_revenue"));
                }
            }
        }
        return stats;
    }

    // Retrieve invoice details summary from view
    public List<String> getInvoiceDetailsSummaryList() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM v_invoice_details_summary ORDER BY invoice_id ASC, detail_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String line = String.format("Mã HĐ: %d | Khách hàng: %s | Sản phẩm: %s | Số lượng: %d | Giá: %.2f | Thành tiền: %.2f | Ngày: %s",
                        rs.getInt("invoice_id"),
                        rs.getString("customer_name"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("subtotal"),
                        rs.getTimestamp("created_at")
                );
                list.add(line);
            }
        }
        return list;
    }

    // Retrieve high-value invoices from view
    public List<Invoice> getHighValueInvoicesList() throws SQLException {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM v_high_value_invoices ORDER BY total_amount DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setId(rs.getInt("invoice_id"));
                invoice.setCustomerName(rs.getString("customer_name"));
                invoice.setTotalAmount(rs.getDouble("total_amount"));
                invoice.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(invoice);
            }
        }
        return list;
    }

    private Invoice mapInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("id"));
        invoice.setCustomerId(rs.getInt("customer_id"));
        invoice.setCreatedAt(rs.getTimestamp("created_at"));
        invoice.setTotalAmount(rs.getDouble("total_amount"));
        try {
            invoice.setCustomerName(rs.getString("customer_name"));
        } catch (SQLException e) {
            // Field customer_name might not exist in basic queries
        }
        return invoice;
    }
}
