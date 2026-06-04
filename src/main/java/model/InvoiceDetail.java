package model;

public class InvoiceDetail {
    private int id;
    private int invoiceId;
    private int productId;
    private int quantity;
    private double unitPrice;

    private String productName;

    public InvoiceDetail() {
    }

    public InvoiceDetail(int id, int invoiceId, int productId, int quantity, double unitPrice) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return String.format("   + Sản phẩm: %s (ID: %d) | Số lượng: %d | Đơn giá: %.2f | Thành tiền: %.2f VNĐ",
                productName != null ? productName : ("ID " + productId), productId, quantity, unitPrice, (quantity * unitPrice));
    }
}
