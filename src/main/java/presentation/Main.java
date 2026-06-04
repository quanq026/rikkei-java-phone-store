package presentation;

import model.Customer;
import model.Invoice;
import model.InvoiceDetail;
import model.Product;
import service.AdminService;
import service.CustomerService;
import service.InvoiceService;
import service.ProductService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AdminService adminService = new AdminService();
    private static final ProductService productService = new ProductService();
    private static final CustomerService customerService = new CustomerService();
    private static final InvoiceService invoiceService = new InvoiceService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=============================================");
            System.out.println("      HỆ THỐNG QUẢN LÝ CỬA HÀNG ĐIỆN THOẠI    ");
            System.out.println("=============================================");
            System.out.println("1. Đăng nhập hệ thống (Admin)");
            System.out.println("2. Thoát");
            System.out.println("=============================================");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleLogin();
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

    private static void handleLogin() {
        System.out.println("\n--------- ĐĂNG NHẬP HỆ THỐNG ---------");
        System.out.print("Tài khoản: ");
        String username = scanner.nextLine().trim();
        System.out.print("Mật khẩu : ");
        String password = scanner.nextLine().trim();

        if (adminService.login(username, password)) {
            System.out.println("Đăng nhập thành công!");
            showMainMenu();
        } else {
            System.out.println("Lỗi: Tài khoản hoặc mật khẩu không chính xác.");
        }
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n================ MENU CHÍNH ================");
            System.out.println("1. Quản lý sản phẩm điện thoại");
            System.out.println("2. Quản lý khách hàng");
            System.out.println("3. Quản lý hóa đơn mua bán");
            System.out.println("4. Thống kê & Báo cáo nâng cao (Views)");
            System.out.println("5. Các thao tác cập nhật/xóa đặc biệt");
            System.out.println("6. Đăng xuất");
            System.out.println("=============================================");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showProductMenu();
                    break;
                case "2":
                    showCustomerMenu();
                    break;
                case "3":
                    showInvoiceMenu();
                    break;
                case "4":
                    showReportMenu();
                    break;
                case "5":
                    showAdvancedOpsMenu();
                    break;
                case "6":
                    System.out.println("Đã đăng xuất thành công.");
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng chọn lại.");
            }
        }
    }

    // Product Management Module
    private static void showProductMenu() {
        while (true) {
            System.out.println("\n---- QUẢN LÝ SẢN PHẨM ----");
            System.out.println("1. Hiển thị danh sách sản phẩm");
            System.out.println("2. Thêm mới sản phẩm (Gọi Stored Procedure)");
            System.out.println("3. Cập nhật thông tin sản phẩm");
            System.out.println("4. Xóa sản phẩm");
            System.out.println("5. Tìm kiếm sản phẩm theo Brand");
            System.out.println("6. Tìm kiếm sản phẩm theo Khoảng giá");
            System.out.println("7. Tìm kiếm sản phẩm theo Tên & Có sẵn");
            System.out.println("8. Quay lại Menu chính");
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
                        Product extProduct = null;
                        int updateId = -1;
                        while (true) {
                            System.out.print("Nhập ID sản phẩm cần cập nhật (hoặc nhập 0 để hủy): ");
                            try {
                                updateId = Integer.parseInt(scanner.nextLine().trim());
                                if (updateId == 0) {
                                    System.out.println("Hủy bỏ thao tác cập nhật.");
                                    break;
                                }
                                extProduct = productService.getById(updateId);
                                if (extProduct != null) {
                                    break;
                                }
                                System.out.println("Lỗi: Sản phẩm không tồn tại. Vui lòng nhập lại!");
                            } catch (NumberFormatException e) {
                                System.out.println("Lỗi: ID phải là số nguyên hợp lệ!");
                            }
                        }
                        if (extProduct == null) break;

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
                        System.out.print("Nhập giá tối thiểu: ");
                        double min = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Nhập giá tối đa: ");
                        double max = Double.parseDouble(scanner.nextLine().trim());
                        productService.searchByPriceRange(min, max).forEach(System.out::println);
                        break;
                    case "7":
                        System.out.print("Nhập từ khóa tên sản phẩm (chỉ hiển thị hàng có sẵn): ");
                        String searchName = scanner.nextLine().trim();
                        productService.searchByNameAndStock(searchName).forEach(System.out::println);
                        break;
                    case "8":
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (Exception e) {
                System.out.println("Lỗi thao tác: " + e.getMessage());
            }
        }
    }

    // Customer Management Module
    private static void showCustomerMenu() {
        while (true) {
            System.out.println("\n---- QUẢN LÝ KHÁCH HÀNG ----");
            System.out.println("1. Hiển thị danh sách khách hàng");
            System.out.println("2. Thêm mới khách hàng (Gọi Stored Procedure)");
            System.out.println("3. Cập nhật thông tin khách hàng");
            System.out.println("4. Xóa khách hàng");
            System.out.println("5. Quay lại Menu chính");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        List<Customer> list = customerService.getAll();
                        if (list.isEmpty()) {
                            System.out.println("Không có khách hàng nào.");
                        } else {
                            list.forEach(System.out::println);
                        }
                        break;
                    case "2":
                        Customer c = new Customer();
                        System.out.print("Nhập tên khách hàng: ");
                        c.setName(scanner.nextLine().trim());
                        System.out.print("Nhập số điện thoại: ");
                        c.setPhone(scanner.nextLine().trim());
                        System.out.print("Nhập địa chỉ email: ");
                        c.setEmail(scanner.nextLine().trim());
                        System.out.print("Nhập địa chỉ: ");
                        c.setAddress(scanner.nextLine().trim());

                        customerService.add(c);
                        System.out.println("Đã thêm khách hàng thành công qua Stored Procedure!");
                        break;
                    case "3":
                        Customer extCust = null;
                        int updateId = -1;
                        while (true) {
                            System.out.print("Nhập ID khách hàng cần cập nhật (hoặc nhập 0 để hủy): ");
                            try {
                                updateId = Integer.parseInt(scanner.nextLine().trim());
                                if (updateId == 0) {
                                    System.out.println("Hủy bỏ thao tác cập nhật.");
                                    break;
                                }
                                extCust = customerService.getById(updateId);
                                if (extCust != null) {
                                    break;
                                }
                                System.out.println("Lỗi: Khách hàng không tồn tại. Vui lòng nhập lại!");
                            } catch (NumberFormatException e) {
                                System.out.println("Lỗi: ID phải là số nguyên hợp lệ!");
                            }
                        }
                        if (extCust == null) break;

                        System.out.println("Thông tin hiện tại: " + extCust);
                        System.out.print("Nhập tên mới (bỏ trống để giữ nguyên): ");
                        String newName = scanner.nextLine().trim();
                        if (!newName.isEmpty()) extCust.setName(newName);

                        System.out.print("Nhập SĐT mới (bỏ trống để giữ nguyên): ");
                        String newPhone = scanner.nextLine().trim();
                        if (!newPhone.isEmpty()) extCust.setPhone(newPhone);

                        System.out.print("Nhập Email mới (bỏ trống để giữ nguyên): ");
                        String newEmail = scanner.nextLine().trim();
                        if (!newEmail.isEmpty()) extCust.setEmail(newEmail);

                        System.out.print("Nhập địa chỉ mới (bỏ trống để giữ nguyên): ");
                        String newAddress = scanner.nextLine().trim();
                        if (!newAddress.isEmpty()) extCust.setAddress(newAddress);

                        customerService.update(extCust);
                        System.out.println("Cập nhật thông tin khách hàng thành công!");
                        break;
                    case "4":
                        Customer deleteCust = null;
                        int deleteId = -1;
                        while (true) {
                            System.out.print("Nhập ID khách hàng cần xóa (hoặc nhập 0 để hủy): ");
                            try {
                                deleteId = Integer.parseInt(scanner.nextLine().trim());
                                if (deleteId == 0) {
                                    System.out.println("Hủy bỏ thao tác xóa.");
                                    break;
                                }
                                deleteCust = customerService.getById(deleteId);
                                if (deleteCust != null) {
                                    break;
                                }
                                System.out.println("Lỗi: Khách hàng không tồn tại. Vui lòng nhập lại!");
                            } catch (NumberFormatException e) {
                                System.out.println("Lỗi: ID phải là số nguyên hợp lệ!");
                            }
                        }
                        if (deleteCust == null) break;

                        System.out.print("Bạn có chắc chắn muốn xóa khách hàng '" + deleteCust.getName() + "' không? (Y/N): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                            customerService.delete(deleteId);
                            System.out.println("Đã xóa khách hàng thành công.");
                        } else {
                            System.out.println("Hủy bỏ thao tác xóa.");
                        }
                        break;
                    case "5":
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    // Invoice Management Module
    private static void showInvoiceMenu() {
        while (true) {
            System.out.println("\n---- QUẢN LÝ HÓA ĐƠN ----");
            System.out.println("1. Hiển thị danh sách tất cả hóa đơn");
            System.out.println("2. Tạo hóa đơn bán hàng mới (Giao dịch Transaction)");
            System.out.println("3. Tìm kiếm hóa đơn (Menu con)");
            System.out.println("4. Quay lại Menu chính");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        List<Invoice> list = invoiceService.getAll();
                        if (list.isEmpty()) {
                            System.out.println("Không có hóa đơn nào.");
                        } else {
                            for (Invoice inv : list) {
                                System.out.println(inv);
                                List<InvoiceDetail> details = invoiceService.getDetailsForInvoice(inv.getId());
                                details.forEach(System.out::println);
                            }
                        }
                        break;
                    case "2":
                        System.out.print("Nhập mã ID khách hàng mua hàng: ");
                        int customerId = Integer.parseInt(scanner.nextLine().trim());
                        Customer cust = customerService.getById(customerId);
                        if (cust == null) {
                            System.out.println("Lỗi: Khách hàng không tồn tại.");
                            break;
                        }

                        Invoice invoice = new Invoice();
                        invoice.setCustomerId(customerId);

                        while (true) {
                            System.out.print("Nhập mã ID sản phẩm chọn mua: ");
                            int productId = Integer.parseInt(scanner.nextLine().trim());
                            Product prod = productService.getById(productId);
                            if (prod == null) {
                                System.out.println("Lỗi: Sản phẩm không tồn tại.");
                                continue;
                            }
                            System.out.print("Nhập số lượng mua (Số lượng còn trong kho: " + prod.getStock() + "): ");
                            int qty = Integer.parseInt(scanner.nextLine().trim());

                            InvoiceDetail detail = new InvoiceDetail();
                            detail.setProductId(productId);
                            detail.setQuantity(qty);
                            detail.setUnitPrice(prod.getPrice());
                            invoice.getDetails().add(detail);

                            System.out.print("Bạn có muốn chọn thêm sản phẩm khác không? (Y/N): ");
                            if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                                break;
                            }
                        }

                        // Save invoice and update inventory within a transaction
                        try {
                            invoiceService.add(invoice);
                            System.out.println("Đã ghi nhận hóa đơn bán hàng thành công!");
                            System.out.println("Thông tin chi tiết: " + invoice);
                        } catch (SQLException ex) {
                            System.out.println("Giao dịch thất bại! Lỗi cơ sở dữ liệu: " + ex.getMessage());
                        }
                        break;
                    case "3":
                        handleSearchInvoice();
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    private static void handleSearchInvoice() {
        while (true) {
            System.out.println("\n---- TÌM KIẾM HÓA ĐƠN ----");
            System.out.println("1. Tìm kiếm theo tên khách hàng gần đúng");
            System.out.println("2. Tìm kiếm theo ngày tháng năm xuất hóa đơn gần đúng");
            System.out.println("3. Quay lại Menu Hóa đơn");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Nhập tên khách hàng cần tìm hóa đơn: ");
                        String custName = scanner.nextLine().trim();
                        List<Invoice> resCust = invoiceService.searchByCustomerName(custName);
                        displayInvoiceResults(resCust);
                        return;
                    case "2":
                        System.out.print("Nhập từ khóa ngày tháng (ví dụ: 09/06/2026 hoặc 2026-06): ");
                        String dateKw = scanner.nextLine().trim();
                        List<Invoice> resDate = invoiceService.searchByDate(dateKw);
                        displayInvoiceResults(resDate);
                        return;
                    case "3":
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ, vui lòng chọn lại.");
                }
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    private static void displayInvoiceResults(List<Invoice> list) throws SQLException {
        if (list.isEmpty()) {
            System.out.println("Không tìm thấy hóa đơn nào khớp.");
        } else {
            for (Invoice inv : list) {
                System.out.println(inv);
                List<InvoiceDetail> details = invoiceService.getDetailsForInvoice(inv.getId());
                details.forEach(System.out::println);
            }
        }
    }

    // Reports and Statistics Module
    private static void showReportMenu() {
        while (true) {
            System.out.println("\n---- BÁO CÁO THỐNG KÊ (VIEWS & STATS) ----");
            System.out.println("1. Thống kê doanh thu (Theo Ngày / Tháng / Năm)");
            System.out.println("2. Xem chi tiết hóa đơn tổng hợp (Truy vấn View v_invoice_details_summary)");
            System.out.println("3. Xem các hóa đơn có giá trị lớn (>1000) (Truy vấn View v_high_value_invoices)");
            System.out.println("4. Quay lại Menu chính");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Chọn loại thống kê (day/month/year): ");
                        String type = scanner.nextLine().trim();
                        Map<String, Double> stats = invoiceService.getRevenueStats(type);
                        if (stats.isEmpty()) {
                            System.out.println("Không có dữ liệu doanh thu.");
                        } else {
                            System.out.println("Thời gian   | Doanh thu (VNĐ)");
                            System.out.println("---------------------------");
                            for (Map.Entry<String, Double> entry : stats.entrySet()) {
                                System.out.printf("%-10s | %,.2f\n", entry.getKey(), entry.getValue());
                            }
                        }
                        break;
                    case "2":
                        List<String> listSummary = invoiceService.getInvoiceDetailsSummaryList();
                        if (listSummary.isEmpty()) {
                            System.out.println("Không có dữ liệu chi tiết hóa đơn.");
                        } else {
                            listSummary.forEach(System.out::println);
                        }
                        break;
                    case "3":
                        List<Invoice> listHigh = invoiceService.getHighValueInvoicesList();
                        if (listHigh.isEmpty()) {
                            System.out.println("Không có hóa đơn giá trị lớn hơn 1000 VNĐ.");
                        } else {
                            listHigh.forEach(System.out::println);
                        }
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    // Advanced Operations Module
    private static void showAdvancedOpsMenu() {
        while (true) {
            System.out.println("\n---- THAO TÁC CẬP NHẬT/XÓA ĐẶC BIỆT ----");
            System.out.println("1. Đồng bộ tính lại tổng số tiền toàn bộ hóa đơn từ chi tiết hóa đơn (Câu 3)");
            System.out.println("2. Xóa các hóa đơn có giá trị nhỏ hơn ngưỡng chỉ định (Câu 4)");
            System.out.println("3. Quay lại Menu chính");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Xác nhận tính lại tổng tiền hóa đơn tự động? (Y/N): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                            invoiceService.updateAllInvoiceTotals();
                            System.out.println("Đã cập nhật đồng bộ tổng tiền hóa đơn thành công!");
                        }
                        break;
                    case "2":
                        System.out.print("Nhập ngưỡng giá trị hóa đơn tối thiểu muốn xóa (VD: 500): ");
                        double threshold = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Bạn có chắc chắn muốn xóa tất cả hóa đơn dưới " + threshold + " không? (Y/N): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                            int deleted = invoiceService.deleteLowValueInvoices(threshold);
                            System.out.println("Đã xóa thành công " + deleted + " hóa đơn nhỏ.");
                        }
                        break;
                    case "3":
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }
}
