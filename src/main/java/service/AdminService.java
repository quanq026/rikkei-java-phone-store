package service;

import dao.AdminDao;

public class AdminService {
    private final AdminDao adminDao;

    public AdminService() {
        this.adminDao = new AdminDao();
    }

    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        return adminDao.login(username.trim(), password);
    }
}
