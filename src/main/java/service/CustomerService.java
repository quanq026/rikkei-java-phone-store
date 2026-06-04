package service;

import dao.CustomerDao;
import model.Customer;
import java.sql.SQLException;

public class CustomerService {
    private final CustomerDao customerDao = new CustomerDao();

    public boolean add(Customer c) throws SQLException {
        if (c.getName() == null || c.getName().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống!");
        }
        return customerDao.add(c);
    }
}
