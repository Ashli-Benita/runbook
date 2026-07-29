package com.runbookagent.dto;

public class AuthResponseDto {
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String role;

    public AuthResponseDto() {}

    public AuthResponseDto(String token, String tokenType, Long id, String username, String email, String role) {
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String tokenType = "Bearer";
        private Long id;
        private String username;
        private String email;
        private String role;

        public Builder token(String token) { this.token = token; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder role(String role) { this.role = role; return this; }

        public AuthResponseDto build() {
            return new AuthResponseDto(token, tokenType, id, username, email, role);
        }
    }
}
