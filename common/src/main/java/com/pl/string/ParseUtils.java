package com.pl.string;

import com.google.common.base.Strings;

/**
 * @author mamad
 * @since 19/12/14.
 */
public final class ParseUtils {
    public static int safeValue(String value, int defaultValue) {
        try {
            return Integer.parseInt(Strings.nullToEmpty(value));
        } catch (NumberFormatException ignored) { // NOSONAR
            return defaultValue;
        }
    }

    public static int minAcceptableValue(String value, int minAcceptableValue) {
        int v = safeValue(value, minAcceptableValue);
        if (v < minAcceptableValue) {
            return minAcceptableValue;
        }
        return v;
    }

    /**
     * Assumed all enums are upper cased
     *
     * @param value        string value
     * @param defaultValue default value if value is empty or invalid
     * @param <E>          type of the enum class
     * @return enum instance
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E safeValue(String value, E defaultValue) {
        if (Strings.isNullOrEmpty(value)) {
            return defaultValue;
        }
        try {
            Class<Enum> classType = (Class<Enum>) defaultValue.getClass();
            return (E) Enum.valueOf(classType, value.toUpperCase());
        } catch (Exception ignored) { // NOSONAR
            return defaultValue;
        }
    }
}
