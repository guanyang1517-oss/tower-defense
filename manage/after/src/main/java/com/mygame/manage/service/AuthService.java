package com.mygame.manage.service;

import com.mygame.manage.dto.CurrentUserResponse;
import com.mygame.manage.dto.LoginRequest;
import com.mygame.manage.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    CurrentUserResponse currentUser();

    void logout(String token);
}
