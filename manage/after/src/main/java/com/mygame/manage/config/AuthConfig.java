package com.mygame.manage.config;

import com.mygame.manage.auth.AuthInterceptor;
import com.mygame.manage.auth.AuthTokenStore;
import com.mygame.manage.repository.AccountRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthConfig implements WebMvcConfigurer {

    private final AuthTokenStore authTokenStore;
    private final AccountRepository accountRepository;

    public AuthConfig(AuthTokenStore authTokenStore, AccountRepository accountRepository) {
        this.authTokenStore = authTokenStore;
        this.accountRepository = accountRepository;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(authTokenStore, accountRepository))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/runtime/**");
    }
}
