package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private int id;
    private int customerId;
    private Timestamp createdAt;
    private double totalAmount;

    private String customerName;
    private List<InvoiceDetail> details = new ArrayList<>();

    public Invoice() {
    }

    public Invoice(int id, int customerId, Timestamp createdAt, double totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<InvoiceDetail> getDetails() {
        return details;
    }

    public void setDetails(List<InvoiceDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return String.format("Mã HĐ: %d | Khách hàng: %s | Ngày tạo: %s | Tổng tiền: %.2f VNĐ",
                id, customerName != null ? customerName : ("ID " + customerId), createdAt, totalAmount);
    }
}
