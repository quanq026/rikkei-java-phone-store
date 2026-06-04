package service;

import dao.ProductDao;
import model.Product;
import java.sql.SQLException;

public class ProductService {
    private final ProductDao productDao = new ProductDao();

    public boolean add(Product p) throws SQLException {
        if (p.getName() == null || p.getName().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        }
        if (p.getBrand() == null || p.getBrand().isEmpty()) {
            throw new IllegalArgumentException("Hãng sản phẩm không được để trống!");
        }
        if (p.getPrice() <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0!");
        }
        if (p.getStock() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được âm!");
        }
        return productDao.add(p);
    }
}
