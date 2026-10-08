package com.mygame.manage.dto;

import java.util.List;

public class AccountCreateRequest {

    private String username;
    private String password;
    private String role;
    private Boolean enabled;
    private List<String> pagePermissions;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getPagePermissions() {
        return pagePermissions;
    }

    public void setPagePermissions(List<String> pagePermissions) {
        this.pagePermissions = pagePermissions;
    }
}
