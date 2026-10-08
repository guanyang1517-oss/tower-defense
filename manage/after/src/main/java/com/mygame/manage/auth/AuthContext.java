package com.mygame.manage.auth;

import com.mygame.manage.entity.AccountEntity;

public final class AuthContext {

    private static final ThreadLocal<AccountEntity> CURRENT = new ThreadLocal<AccountEntity>();

    private AuthContext() {
    }

    public static void set(AccountEntity account) {
        CURRENT.set(account);
    }

    public static AccountEntity get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
