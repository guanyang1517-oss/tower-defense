package com.mygame.manage.auth;

import com.mygame.manage.common.ApiException;
import com.mygame.manage.entity.AccountEntity;
import com.mygame.manage.repository.AccountRepository;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    private final AuthTokenStore authTokenStore;
    private final AccountRepository accountRepository;

    public AuthInterceptor(AuthTokenStore authTokenStore, AccountRepository accountRepository) {
        this.authTokenStore = authTokenStore;
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            throw new ApiException("未登录或登录已失效");
        }
        Long accountId = authTokenStore.getAccountId(token.trim());
        if (accountId == null) {
            throw new ApiException("未登录或登录已失效");
        }
        AccountEntity account = accountRepository.findById(accountId).orElseThrow(() -> new ApiException("账号不存在"));
        if (!Boolean.TRUE.equals(account.getEnabled())) {
            throw new ApiException("账号已被禁用");
        }
        AuthContext.set(account);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
