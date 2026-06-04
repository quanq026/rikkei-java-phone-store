package service;

import dao.CustomerDao;
import model.Customer;
import java.sql.SQLException;
import java.util.List;

public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService() {
        this.customerDao = new CustomerDao();
    }

    public List<Customer> getAll() throws SQLException {
        return customerDao.getAll();
    }

    public Customer getById(int id) throws SQLException {
        return customerDao.getById(id);
    }

    public boolean add(Customer c) throws SQLException, IllegalArgumentException {
        validateCustomer(c);
        List<Customer> all = customerDao.getAll();
        for (Customer cust : all) {
            if (c.getEmail().trim().equalsIgnoreCase(cust.getEmail())) {
                throw new IllegalArgumentException("Email '" + c.getEmail() + "' đã được đăng ký bởi khách hàng khác.");
            }
        }
        return customerDao.add(c);
    }

    public boolean update(Customer c) throws SQLException, IllegalArgumentException {
        if (customerDao.getById(c.getId()) == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng có ID = " + c.getId() + " để cập nhật.");
        }
        validateCustomer(c);
        List<Customer> all = customerDao.getAll();
        for (Customer cust : all) {
            if (cust.getId() != c.getId() && c.getEmail().trim().equalsIgnoreCase(cust.getEmail())) {
                throw new IllegalArgumentException("Email '" + c.getEmail() + "' đã được đăng ký bởi khách hàng khác.");
            }
        }
        return customerDao.update(c);
    }

    public boolean delete(int id) throws SQLException, IllegalArgumentException {
        if (customerDao.getById(id) == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng có ID = " + id + " để xóa.");
        }
        return customerDao.delete(id);
    }

    private void validateCustomer(Customer c) throws IllegalArgumentException {
        if (c.getName() == null || c.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }
        if (c.getPhone() != null && !c.getPhone().trim().isEmpty()) {
            if (!c.getPhone().trim().matches("\\d{9,12}")) {
                throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải gồm 9-12 chữ số).");
            }
        }
        if (c.getEmail() == null || !c.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Định dạng Email không hợp lệ.");
        }
        if (c.getAddress() == null || c.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ không được để trống.");
        }
    }
}
