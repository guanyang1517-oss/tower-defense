package com.mygame.manage.service.impl;

import com.mygame.manage.auth.AuthContext;
import com.mygame.manage.common.ApiException;
import com.mygame.manage.dto.AccountCreateRequest;
import com.mygame.manage.dto.AccountResponse;
import com.mygame.manage.dto.AccountUpdateRequest;
import com.mygame.manage.entity.AccountEntity;
import com.mygame.manage.repository.AccountRepository;
import com.mygame.manage.service.AccountService;
import com.mygame.manage.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AccountServiceImpl implements AccountService {

    private static final List<String> USER_PAGE_CODES = Collections.unmodifiableList(Arrays.asList("config", "enemy", "level", "wave"));

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountResponse> list() {
        requireAdmin();
        List<AccountResponse> result = new ArrayList<AccountResponse>();
        List<AccountEntity> accounts = accountRepository.findAll();
        for (AccountEntity account : accounts) {
            result.add(toResponse(account));
        }
        return result;
    }

    @Override
    public AccountResponse create(AccountCreateRequest request) {
        requireAdmin();
        validateCreate(request);
        if (accountRepository.findByUsername(request.getUsername().trim()).isPresent()) {
            throw new ApiException("账号已存在");
        }
        AccountEntity entity = new AccountEntity();
        entity.setUsername(request.getUsername().trim());
        entity.setPassword(PasswordUtil.sha256(request.getPassword()));
        entity.setRole(normalizeRole(request.getRole()));
        entity.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
        entity.setPagePermissions(joinPermissions(request.getPagePermissions()));
        return toResponse(accountRepository.save(entity));
    }

    @Override
    public AccountResponse update(Long id, AccountUpdateRequest request) {
        requireAdmin();
        if (request == null) {
            throw new ApiException("请求参数不能为空");
        }
        AccountEntity entity = accountRepository.findById(id).orElseThrow(() -> new ApiException("账号不存在"));
        String role = normalizeRole(request.getRole());
        Boolean enabled = request.getEnabled();
        if (enabled == null) {
            throw new ApiException("启用状态不能为空");
        }
        if (!isBlank(request.getPassword())) {
            entity.setPassword(PasswordUtil.sha256(request.getPassword().trim()));
        }
        entity.setRole(role);
        entity.setEnabled(enabled);
        entity.setPagePermissions(joinPermissions(request.getPagePermissions()));
        validateAdminRetention(entity);
        return toResponse(accountRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        requireAdmin();
        AccountEntity entity = accountRepository.findById(id).orElseThrow(() -> new ApiException("账号不存在"));
        validateAdminDelete(entity);
        accountRepository.delete(entity);
    }

    private void validateCreate(AccountCreateRequest request) {
        if (request == null) {
            throw new ApiException("请求参数不能为空");
        }
        if (isBlank(request.getUsername())) {
            throw new ApiException("账号不能为空");
        }
        if (isBlank(request.getPassword())) {
            throw new ApiException("密码不能为空");
        }
        if (request.getEnabled() == null) {
            throw new ApiException("启用状态不能为空");
        }
        normalizeRole(request.getRole());
    }

    private void validateAdminRetention(AccountEntity entity) {
        AccountEntity current = requireAdmin();
        if (!"ADMIN".equals(current.getRole())) {
            return;
        }
        if ("ADMIN".equals(entity.getRole()) && Boolean.TRUE.equals(entity.getEnabled())) {
            return;
        }
        if (!current.getId().equals(entity.getId())) {
            return;
        }
        long adminCount = accountRepository.countByRoleAndEnabled("ADMIN", true);
        if (adminCount <= 1L) {
            throw new ApiException("至少需要保留一个启用中的管理员账号");
        }
    }

    private void validateAdminDelete(AccountEntity entity) {
        if (!"ADMIN".equals(entity.getRole()) || !Boolean.TRUE.equals(entity.getEnabled())) {
            return;
        }
        long adminCount = accountRepository.countByRoleAndEnabled("ADMIN", true);
        if (adminCount <= 1L) {
            throw new ApiException("至少需要保留一个启用中的管理员账号");
        }
    }

    private AccountResponse toResponse(AccountEntity entity) {
        AccountResponse response = new AccountResponse();
        response.setId(entity.getId());
        response.setUsername(entity.getUsername());
        response.setRole(entity.getRole());
        response.setEnabled(entity.getEnabled());
        response.setPagePermissions(splitPermissions(entity.getPagePermissions()));
        return response;
    }

    private AccountEntity requireAdmin() {
        AccountEntity account = AuthContext.get();
        if (account == null) {
            throw new ApiException("未登录或登录已失效");
        }
        if (!"ADMIN".equals(account.getRole())) {
            throw new ApiException("无权限访问该功能");
        }
        return account;
    }

    private String normalizeRole(String role) {
        if ("ADMIN".equals(role) || "USER".equals(role)) {
            return role;
        }
        throw new ApiException("角色只能是 ADMIN 或 USER");
    }

    private String joinPermissions(List<String> pagePermissions) {
        if (pagePermissions == null || pagePermissions.isEmpty()) {
            return "";
        }
        Set<String> result = new LinkedHashSet<String>();
        for (String permission : pagePermissions) {
            if (USER_PAGE_CODES.contains(permission)) {
                result.add(permission);
            }
        }
        return String.join(",", result);
    }

    private List<String> splitPermissions(String pagePermissions) {
        if (isBlank(pagePermissions)) {
            return new ArrayList<String>();
        }
        List<String> result = new ArrayList<String>();
        for (String permission : pagePermissions.split(",")) {
            if (USER_PAGE_CODES.contains(permission)) {
                result.add(permission);
            }
        }
        return result;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
