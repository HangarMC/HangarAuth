package io.papermc.paperauth.model.request;

public final class LoginRequest {

    private String username;
    private String password;

    public LoginRequest() {
        // jackson
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        //noinspection SuspiciousRegexArgument
        return "LoginRequest{" +
               "username='" + username + '\'' +
               ", password='" + password.replaceAll(".", "*") + '\'' +
               '}';
    }
}
