package com.pl.dsl;

/**
 * @author mamad
 * @since 29/11/14.
 */
public class InvalidRequestException extends Exception {
    public InvalidRequestException(String message) {
        super(message);
    }
}
