package com.piedpiper.graphqldemo;

public interface AuthorRepositoryCustom {
    void cascadingDeleteAccount(String accountID) throws IdNotFoundException;
}
