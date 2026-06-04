package service;

import dao.ProductDao;
import model.Product;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDao productDao;

    public ProductService() {
        this.productDao = new ProductDao();
    }

    public List<Product> getAll() throws SQLException {
        return productDao.getAll();
    }

    public Product getById(int id) throws SQLException {
        return productDao.getById(id);
    }

    public boolean add(Product p) throws SQLException, IllegalArgumentException {
        validateProduct(p);
        return productDao.add(p);
    }

    public boolean update(Product p) throws SQLException, IllegalArgumentException {
        if (productDao.getById(p.getId()) == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có ID = " + p.getId() + " để cập nhật.");
        }
        validateProduct(p);
        return productDao.update(p);
    }

    public boolean delete(int id) throws SQLException, IllegalArgumentException {
        if (productDao.getById(id) == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có ID = " + id + " để xóa.");
        }
        return productDao.delete(id);
    }

    public List<Product> searchByBrand(String brand) throws SQLException {
        if (brand == null || brand.trim().isEmpty()) {
            return productDao.getAll();
        }
        return productDao.searchByBrand(brand.trim());
    }

    public List<Product> searchByPriceRange(double min, double max) throws SQLException, IllegalArgumentException {
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Giá tìm kiếm không được âm.");
        }
        if (min > max) {
            throw new IllegalArgumentException("Khoảng giá tối thiểu phải nhỏ hơn hoặc bằng giá tối đa.");
        }
        return productDao.searchByPriceRange(min, max);
    }

    public List<Product> searchByNameAndStock(String nameKeyword) throws SQLException {
        if (nameKeyword == null || nameKeyword.trim().isEmpty()) {
            return productDao.getAll();
        }
        return productDao.searchByNameAndStock(nameKeyword.trim());
    }

    private void validateProduct(Product p) throws IllegalArgumentException {
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
        }
        if (p.getBrand() == null || p.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Hãng sản phẩm không được để trống.");
        }
        if (p.getPrice() <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0.");
        }
        if (p.getStock() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được âm.");
        }
    }
}
