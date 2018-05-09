package com.trialty.exception;

public class UnsupportedImageException extends RuntimeException {
    public UnsupportedImageException() {super();}

    public UnsupportedImageException(String message) {
        super(message);
    }

    public UnsupportedImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
