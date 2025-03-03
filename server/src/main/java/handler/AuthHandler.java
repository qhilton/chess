package handler;

import service.AuthService;

public class AuthHandler {
    AuthService authService = new AuthService();

    public void clear() {
        authService.clear();
    }
}
