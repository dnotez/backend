package com.pl.bean;

/**
 * @author mamad
 * @since 13/12/14.
 */
public class InvalidValueException extends Exception {
    private final Object value;

    public InvalidValueException(String message, Object value) {
        super(message);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getWithValueTypeMessage() {
        return String.format("Value:%s, type:%s, message:%s", value, value != null ? value.getClass().getName() : null, getMessage());
    }
}
