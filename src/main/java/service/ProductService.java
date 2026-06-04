package service;

import dao.ProductDao;
import model.Product;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDao productDao = new ProductDao();

    public List<Product> getAll() throws SQLException { return productDao.getAll(); }
    public Product getById(int id) throws SQLException { return productDao.getById(id); }
    public boolean add(Product p) throws SQLException { validate(p); return productDao.add(p); }
    public boolean update(Product p) throws SQLException { validate(p); return productDao.update(p); }
    public boolean delete(int id) throws SQLException { return productDao.delete(id); }
    public List<Product> searchByBrand(String brand) throws SQLException { return productDao.searchByBrand(brand); }
    public List<Product> searchByPriceRange(double min, double max) throws SQLException { return productDao.searchByPriceRange(min, max); }
    public List<Product> searchByNameAndStock(String kw) throws SQLException { return productDao.searchByNameAndStock(kw); }

    private void validate(Product p) {
        if (p.getName() == null || p.getName().isEmpty()) throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        if (p.getBrand() == null || p.getBrand().isEmpty()) throw new IllegalArgumentException("Hãng sản phẩm không được để trống!");
        if (p.getPrice() <= 0) throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0!");
        if (p.getStock() < 0) throw new IllegalArgumentException("Số lượng tồn kho không được âm!");
    }
}
