package presentation;

import model.Product;
import service.ProductService;
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
            System.out.println("1. Thêm mới sản phẩm (Gọi Stored Procedure)");
            System.out.println("2. Quay lại Menu chính");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
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
                    case "2":
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
