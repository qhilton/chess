package service;

import dataaccess.AuthDAO;

public class AuthService {
    AuthDAO auth = new AuthDAO();

    public void clear() {
        auth.clear();
    }
}
