package com.smirnov.api.exceptions;

public class ClientIllegalSymbols extends Exception {
    public static final String message = "Использованы запрещенные символы";
    public ClientIllegalSymbols(String message) {
        super(message);
    }

}