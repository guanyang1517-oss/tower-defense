package com.mygame.manage.controller;

import com.mygame.manage.common.ApiResponse;
import com.mygame.manage.dto.AccountCreateRequest;
import com.mygame.manage.dto.AccountResponse;
import com.mygame.manage.dto.AccountUpdateRequest;
import com.mygame.manage.service.AccountService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ApiResponse<List<AccountResponse>> list() {
        return ApiResponse.ok(accountService.list());
    }

    @PostMapping
    public ApiResponse<AccountResponse> create(@RequestBody AccountCreateRequest request) {
        return ApiResponse.ok(accountService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AccountResponse> update(@PathVariable Long id, @RequestBody AccountUpdateRequest request) {
        return ApiResponse.ok(accountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ApiResponse.ok(null);
    }
}
