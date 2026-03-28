package com.reboot.lets_play.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
