package edu.univ.erp.auth;

public class LoginResult {
    public boolean ok;
    public String error;
    public int userId;
    public String role;

    private LoginResult(boolean ok, String error, int userId, String role) {
        this.ok = ok;
        this.error = error;
        this.userId = userId;
        this.role = role;
    }

    public static LoginResult success(int id, String role) {
        return new LoginResult(true, null, id, role);
    }

    public static LoginResult error(String msg) {
        return new LoginResult(false, msg, -1, null);
    }
}
