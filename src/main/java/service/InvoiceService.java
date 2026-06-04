package service;

import dao.InvoiceDao;
import dao.ProductDao;
import dao.CustomerDao;
import model.Invoice;
import model.InvoiceDetail;
import model.Product;
import model.Customer;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class InvoiceService {
    private final InvoiceDao invoiceDao;
    private final ProductDao productDao;
    private final CustomerDao customerDao;

    public InvoiceService() {
        this.invoiceDao = new InvoiceDao();
        this.productDao = new ProductDao();
        this.customerDao = new CustomerDao();
    }

    public List<Invoice> getAll() throws SQLException {
        return invoiceDao.getAll();
    }

    public List<InvoiceDetail> getDetailsForInvoice(int invoiceId) throws SQLException {
        return invoiceDao.getDetailsForInvoice(invoiceId);
    }

    public boolean add(Invoice invoice) throws SQLException, IllegalArgumentException {
        Customer customer = customerDao.getById(invoice.getCustomerId());
        if (customer == null) {
            throw new IllegalArgumentException("Khách hàng có ID = " + invoice.getCustomerId() + " không tồn tại.");
        }
        if (invoice.getDetails() == null || invoice.getDetails().isEmpty()) {
            throw new IllegalArgumentException("Hóa đơn phải có ít nhất một sản phẩm.");
        }

        double total = 0.0;
        for (InvoiceDetail d : invoice.getDetails()) {
            Product p = productDao.getById(d.getProductId());
            if (p == null) {
                throw new IllegalArgumentException("Sản phẩm có ID = " + d.getProductId() + " không tồn tại.");
            }
            if (d.getQuantity() <= 0) {
                throw new IllegalArgumentException("Số lượng mua của sản phẩm '" + p.getName() + "' phải lớn hơn 0.");
            }
            d.setUnitPrice(p.getPrice());
            total += d.getUnitPrice() * d.getQuantity();
        }
        invoice.setTotalAmount(total);

        return invoiceDao.add(invoice);
    }

    public List<Invoice> searchByCustomerName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return invoiceDao.getAll();
        }
        return invoiceDao.searchByCustomerName(name.trim());
    }

    public List<Invoice> searchByDate(String dateKeyword) throws SQLException {
        if (dateKeyword == null || dateKeyword.trim().isEmpty()) {
            return invoiceDao.getAll();
        }
        return invoiceDao.searchByDate(dateKeyword.trim());
    }

    public Map<String, Double> getRevenueStats(String type) throws SQLException {
        if (type == null || type.trim().isEmpty()) {
            type = "day";
        }
        return invoiceDao.getRevenueStats(type.trim().toLowerCase());
    }

    public boolean updateAllInvoiceTotals() throws SQLException {
        return invoiceDao.updateAllInvoiceTotals();
    }

    public int deleteLowValueInvoices(double threshold) throws SQLException {
        if (threshold < 0) {
            throw new IllegalArgumentException("Mức giới hạn tiền không được âm.");
        }
        return invoiceDao.deleteLowValueInvoices(threshold);
    }

    public List<String> getInvoiceDetailsSummaryList() throws SQLException {
        return invoiceDao.getInvoiceDetailsSummaryList();
    }

    public List<Invoice> getHighValueInvoicesList() throws SQLException {
        return invoiceDao.getHighValueInvoicesList();
    }
}
