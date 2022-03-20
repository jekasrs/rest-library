package com.smirnov.api.exceptions;

public class BookAlreadyExist extends Exception {
    public BookAlreadyExist(String message) {
        super(message);
    }
}
