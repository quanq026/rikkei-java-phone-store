package presentation;

import model.Product;
import service.ProductService;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductService productService = new ProductService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=============================================");
            System.out.println("      HỆ THỐNG QUẢN LÝ CỬA HÀNG ĐIỆN THOẠI    ");
            System.out.println("=============================================");
            System.out.println("1. Quản lý sản phẩm điện thoại");
            System.out.println("2. Thoát");
            System.out.println("=============================================");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showProductMenu();
                    break;
                case "2":
                    System.out.println("Cảm ơn bạn đã sử dụng chương trình. Tạm biệt!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng chọn lại.");
            }
        }
    }

    private static void showProductMenu() {
        while (true) {
            System.out.println("\n---- QUẢN LÝ SẢN PHẨM ----");
            System.out.println("1. Hiển thị danh sách sản phẩm");
            System.out.println("2. Thêm mới sản phẩm (Gọi Stored Procedure)");
            System.out.println("3. Cập nhật thông tin sản phẩm");
            System.out.println("4. Xóa sản phẩm");
            System.out.println("5. Tìm kiếm sản phẩm theo Brand");
            System.out.println("6. Quay lại Menu chính");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        List<Product> products = productService.getAll();
                        if (products.isEmpty()) {
                            System.out.println("Không có sản phẩm nào trong hệ thống.");
                        } else {
                            products.forEach(System.out::println);
                        }
                        break;
                    case "2":
                        Product p = new Product();
                        System.out.print("Nhập tên sản phẩm: ");
                        p.setName(scanner.nextLine().trim());
                        System.out.print("Nhập hãng (Brand): ");
                        p.setBrand(scanner.nextLine().trim());
                        System.out.print("Nhập đơn giá: ");
                        p.setPrice(Double.parseDouble(scanner.nextLine().trim()));
                        System.out.print("Nhập số lượng tồn kho: ");
                        p.setStock(Integer.parseInt(scanner.nextLine().trim()));

                        productService.add(p);
                        System.out.println("Đã thêm sản phẩm thành công qua Stored Procedure!");
                        break;
                    case "3":
                        System.out.print("Nhập ID sản phẩm cần cập nhật: ");
                        int updateId = Integer.parseInt(scanner.nextLine().trim());
                        Product extProduct = productService.getById(updateId);
                        if (extProduct == null) {
                            System.out.println("Sản phẩm không tồn tại.");
                            break;
                        }
                        System.out.println("Thông tin hiện tại: " + extProduct);
                        System.out.print("Nhập tên mới (bỏ trống để giữ nguyên): ");
                        String newName = scanner.nextLine().trim();
                        if (!newName.isEmpty()) extProduct.setName(newName);

                        System.out.print("Nhập hãng mới (bỏ trống để giữ nguyên): ");
                        String newBrand = scanner.nextLine().trim();
                        if (!newBrand.isEmpty()) extProduct.setBrand(newBrand);

                        System.out.print("Nhập đơn giá mới (-1 để giữ nguyên): ");
                        double newPrice = Double.parseDouble(scanner.nextLine().trim());
                        if (newPrice >= 0) extProduct.setPrice(newPrice);

                        System.out.print("Nhập tồn kho mới (-1 để giữ nguyên): ");
                        int newStock = Integer.parseInt(scanner.nextLine().trim());
                        if (newStock >= 0) extProduct.setStock(newStock);

                        productService.update(extProduct);
                        System.out.println("Cập nhật thông tin sản phẩm thành công!");
                        break;
                    case "4":
                        System.out.print("Nhập ID sản phẩm cần xóa: ");
                        int deleteId = Integer.parseInt(scanner.nextLine().trim());
                        Product deleteProduct = productService.getById(deleteId);
                        if (deleteProduct == null) {
                            System.out.println("Sản phẩm không tồn tại.");
                            break;
                        }
                        System.out.print("Bạn có chắc chắn muốn xóa sản phẩm '" + deleteProduct.getName() + "' không? (Y/N): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                            productService.delete(deleteId);
                            System.out.println("Đã xóa sản phẩm thành công.");
                        } else {
                            System.out.println("Hủy bỏ thao tác xóa.");
                        }
                        break;
                    case "5":
                        System.out.print("Nhập hãng (Brand) cần tìm: ");
                        String searchBrand = scanner.nextLine().trim();
                        productService.searchByBrand(searchBrand).forEach(System.out::println);
                        break;
                    case "6":
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (Exception e) {
                System.out.println("Lỗi thao tác: " + e.getMessage());
            }
        }
    }
}
