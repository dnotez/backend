package com.pl.bean;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Helper class to check fields of DTO (Data Transfer Object) beans such as request objects in REST api methods.
 *
 * @author mamad
 * @since 13/12/14.
 */
public class ArgChecker {
    private final List<VerifyMessage> verifications = Lists.newArrayList();

    public static ArgChecker create() {
        return new ArgChecker();
    }

    public <T> ArgChecker notNull(T value, String message) {
        return add(Objects::nonNull, value, message);
    }

    public ArgChecker notEmpty(String value, String message) {
        return add(str -> !Strings.isNullOrEmpty(str), value, message);
    }

    public <T> ArgChecker anyOf(T value, String message, List<T> acceptedValues) {
        return anyOf(value, message, acceptedValues, false);
    }

    public <T> ArgChecker anyOf(T value, String message, List<T> acceptedValues, boolean appendValuesToMessage) {
        return notNull(acceptedValues, "Accepted values can not be null.")
                .add(v -> acceptedValues != null && acceptedValues.contains(v), value,
                        appendValuesToMessage ? message + " " + acceptedValues : message);
    }

    public <T> ArgChecker nullOr(T value, String message, List<T> acceptedValues) {
        return nullOr(value, message, acceptedValues, false);
    }

    public ArgChecker nullOrNumber(String value, String message) {
        return add(v -> (v == null || v.matches("-?\\d+(\\.\\d+)?")), value, message);
    }

    public ArgChecker isNumber(String value, String message) {
        return add(v -> (v != null && v.trim().length() > 0 && v.matches("-?\\d+(\\.\\d+)?")), value, message);
    }

    public <T> ArgChecker nullOr(T value, String message, List<T> acceptedValues, boolean appendValuesToMessage) {
        if (value != null) {
            return anyOf(value, message, acceptedValues, appendValuesToMessage);
        }
        return this;
    }

    public <T> ArgChecker add(Predicate<T> predicate, T value, String message) {
        this.verifications.add(new VerifyMessage<>(predicate, value, message));
        return this;
    }

    public void verify() throws InvalidValueException {
        for (VerifyMessage verification : verifications) {
            verification.verify();
        }
    }

    private static class VerifyMessage<T> {
        private final Predicate<T> predicate;
        private final T value;
        private final String message;

        public VerifyMessage(Predicate<T> predicate, T value, String message) {
            this.predicate = predicate;
            this.value = value;
            this.message = message;
        }

        public void verify() throws InvalidValueException {
            if (!predicate.test(value)) {
                throw new InvalidValueException(message, value);
            }
        }
    }
}
