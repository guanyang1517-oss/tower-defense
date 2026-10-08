package com.mygame.manage.service;

import com.mygame.manage.dto.AccountCreateRequest;
import com.mygame.manage.dto.AccountResponse;
import com.mygame.manage.dto.AccountUpdateRequest;

import java.util.List;

public interface AccountService {

    List<AccountResponse> list();

    AccountResponse create(AccountCreateRequest request);

    AccountResponse update(Long id, AccountUpdateRequest request);

    void delete(Long id);
}
