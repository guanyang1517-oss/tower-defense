package com.mygame.manage.dto;

import java.util.List;

public class AccountResponse {

    private Long id;
    private String username;
    private String role;
    private Boolean enabled;
    private List<String> pagePermissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
