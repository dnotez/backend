package com.pl.dsl;

/**
 * @author mamad
 * @since 23/11/14.
 */
public abstract class GeneralResponse<T> {
    protected boolean error;
    //error message to display to user, if save was not successful
    protected String errorMessage;

    public GeneralResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        this.error = true;
    }

    public GeneralResponse() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public abstract T error(String message);
}
