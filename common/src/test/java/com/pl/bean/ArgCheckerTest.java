package com.pl.bean;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author mamad
 * @since 18/12/14.
 */
public class ArgCheckerTest {
    @Test
    public void testNotNull() throws Exception {
        try {
            ArgChecker.create().notNull(null, "Must throw an exception").verify();
        } catch (InvalidValueException ignored) {
            assertThat(ignored.getMessage(), equalTo("Must throw an exception"));
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create()
                    .notNull(1, "Arg can not be null")
                    .notNull(true, "Arg can not be null")
                    .notNull("", "Arg can not be null")
                    .notNull(1L, "Arg can not be null")
                    .notNull(12.23f, "Arg can not be null")
                    .notNull(ArgChecker.create(), "Arg can not be null")
                    .verify();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testNotEmpty() throws Exception {
        try {
            ArgChecker.create().notEmpty(null, "Must throw an exception").verify();
        } catch (InvalidValueException ignored) {
            assertThat(ignored.getMessage(), equalTo("Must throw an exception"));
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create().notEmpty("", "Must throw an exception").verify();
        } catch (InvalidValueException ignored) {
            assertThat(ignored.getMessage(), equalTo("Must throw an exception"));
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create()
                    .notEmpty("id", "Arg can not be empty")
                    .notNull(true, "Arg can not be null")
                    .notNull("", "Arg can not be null")
                    .notNull(1L, "Arg can not be null")
                    .notNull(12.23f, "Arg can not be null")
                    .notNull(ArgChecker.create(), "Arg can not be null")
                    .notEmpty("key", "Key can not be empty")
                    .verify();
        } catch (InvalidValueException e) {
            fail(e.getWithValueTypeMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAnyOf() throws Exception {
        try {
            ArgChecker.create().anyOf(null, "Must throw an exception", null).verify();
        } catch (InvalidValueException ignored) {
            assertThat(ignored.getMessage(), equalTo("Accepted values can not be null."));
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create().anyOf(null, "Must throw an exception", ImmutableList.of()).verify();
        } catch (InvalidValueException ignored) {
            assertThat(ignored.getMessage(), equalTo("Must throw an exception"));
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create().anyOf(0, "Must be 1 or 2", ImmutableList.of(1, 2)).verify();
        } catch (InvalidValueException ignored) {
            assertThat(ignored.getMessage(), equalTo("Must be 1 or 2"));
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create().anyOf("id", "Must be id or label", ImmutableList.of("id", "label")).verify();
        } catch (InvalidValueException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create()
                    .notEmpty("id", "Arg can not be empty")
                    .notNull(true, "Arg can not be null")
                    .anyOf(12.0f, "Arg must be 12", ImmutableList.of(12.0f))
                    .notNull(ArgChecker.create(), "Arg can not be null")
                    .notEmpty("key", "Key can not be empty")
                    .verify();
        } catch (InvalidValueException e) {
            fail(e.getWithValueTypeMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testNullOr() throws Exception {
        try {
            ArgChecker.create().nullOr(null, "Must not throw an exception", null).verify();
        } catch (InvalidValueException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create().nullOr(null, "Must not throw an exception", ImmutableList.of()).verify();
        } catch (InvalidValueException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create().nullOr("id", "Must throw an exception", ImmutableList.of()).verify();
        } catch (InvalidValueException ignored) {
            assertThat(ignored.getMessage(), equalTo("Must throw an exception"));
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create()
                    .nullOr(null, "Arg can not be empty", ImmutableList.of(2, 3))
                    .notEmpty("id", "Arg can not be empty")
                    .notNull(true, "Arg can not be null")
                    .nullOr(3, "Arg can not be empty", ImmutableList.of(2, 3))
                    .anyOf(12.0f, "Arg must be 12", ImmutableList.of(12.0f))
                    .notNull(ArgChecker.create(), "Arg can not be null")
                    .notEmpty("key", "Key can not be empty")
                    .verify();
        } catch (InvalidValueException e) {
            fail(e.getWithValueTypeMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testIsNumber() throws Exception {
        try {
            ArgChecker.create()
                    .isNumber(null, "Must throw an exception")
                    .isNumber("", "Must throw an exception")
                    .isNumber(Double.toString(Double.MAX_VALUE), "Must throw an exception")
                    .verify();
        } catch (InvalidValueException e) {
            assertThat(e.getMessage(), equalTo("Must throw an exception"));
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create()
                    .isNumber("12", "Arg can not be empty")
                    .isNumber("-12", "Arg can not be empty")
                    .isNumber("1.2", "Arg can not be empty")
                    .isNumber("-1.2", "Arg can not be empty")
                    .notEmpty("id", "Arg can not be empty")
                    .notNull(true, "Arg can not be null")
                    .nullOr(3, "Arg can not be empty", ImmutableList.of(2, 3))
                    .anyOf(12.0f, "Arg must be 12", ImmutableList.of(12.0f))
                    .notNull(ArgChecker.create(), "Arg can not be null")
                    .notEmpty("key", "Key can not be empty")
                    .verify();
        } catch (InvalidValueException e) {
            fail(e.getWithValueTypeMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testNullOrNumber() throws Exception {
        try {
            ArgChecker.create()
                    .nullOrNumber("", "Must throw an exception")
                    .nullOrNumber(Double.toString(Double.MAX_VALUE), "Must throw an exception")
                    .verify();
        } catch (InvalidValueException e) {
            assertThat(e.getMessage(), equalTo("Must throw an exception"));
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

        try {
            ArgChecker.create()
                    .nullOrNumber(null, "Must not throw an exception")
                    .nullOrNumber("12", "Must not throw an exception")
                    .verify();
        } catch (InvalidValueException e) {
            fail(e.getWithValueTypeMessage());
        } catch (Exception e) {
            fail("Must not throw generic exception");
        }

    }
}
