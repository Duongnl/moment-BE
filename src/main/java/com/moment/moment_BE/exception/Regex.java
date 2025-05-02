package com.moment.moment_BE.exception;

import org.springframework.http.HttpStatus;

public final class Regex {

    private Regex() {}

    public static final String USER_NAME = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";
    public static final String PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!.@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    public static final String EMAIL = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    public static final String SEX = "^(male|female|other)$";
    public static final String NOTI_STATUS = "^(unread|all)$";
    public static final String NAME = "^(?=(.*\\p{L}){2,})[\\p{L} ]{2,255}$";
    public static final String USER_NAME_EMAIL = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9](@gmail\\.com)?$";




}
