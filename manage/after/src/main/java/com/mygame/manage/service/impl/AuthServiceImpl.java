package com.mygame.manage.service.impl;

import com.mygame.manage.auth.AuthContext;
import com.mygame.manage.auth.AuthTokenStore;
import com.mygame.manage.common.ApiException;
import com.mygame.manage.dto.CurrentUserResponse;
import com.mygame.manage.dto.LoginRequest;
import com.mygame.manage.dto.LoginResponse;
import com.mygame.manage.entity.AccountEntity;
import com.mygame.manage.repository.AccountRepository;
import com.mygame.manage.service.AuthService;
import com.mygame.manage.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final List<String> USER_PAGE_CODES = Collections.unmodifiableList(Arrays.asList("config", "enemy", "level", "wave"));

    private final AccountRepository accountRepository;
    private final AuthTokenStore authTokenStore;

    public AuthServiceImpl(AccountRepository accountRepository, AuthTokenStore authTokenStore) {
        this.accountRepository = accountRepository;
        this.authTokenStore = authTokenStore;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request == null || isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            throw new ApiException("账号和密码不能为空");
        }
        AccountEntity account = accountRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new ApiException("账号或密码错误"));
        if (!Boolean.TRUE.equals(account.getEnabled())) {
            throw new ApiException("账号已被禁用");
        }
        if (!PasswordUtil.sha256(request.getPassword()).equals(account.getPassword())) {
            throw new ApiException("账号或密码错误");
        }
        String token = authTokenStore.createToken(account.getId());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setId(account.getId());
        response.setUsername(account.getUsername());
        response.setRole(account.getRole());
        response.setPagePermissions(splitPermissions(account.getPagePermissions()));
        return response;
    }

    @Override
    public CurrentUserResponse currentUser() {
        AccountEntity account = requireCurrentUser();
        CurrentUserResponse response = new CurrentUserResponse();
        response.setId(account.getId());
        response.setUsername(account.getUsername());
        response.setRole(account.getRole());
        response.setPagePermissions(splitPermissions(account.getPagePermissions()));
        return response;
    }

    @Override
    public void logout(String token) {
        if (token != null && !token.trim().isEmpty()) {
            authTokenStore.removeToken(token.trim());
        }
    }

    private AccountEntity requireCurrentUser() {
        AccountEntity account = AuthContext.get();
        if (account == null) {
            throw new ApiException("未登录或登录已失效");
        }
        return account;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
}
