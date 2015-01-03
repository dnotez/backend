package com.pl.dsl.extension;

import com.pl.dsl.GeneralResponse;

/**
 * @author mamad
 * @since 23/11/14.
 */
public class GetResponse extends GeneralResponse<GetResponse> {
    private boolean alreadySaved;

    public GetResponse(String errorMessage) {
        super(errorMessage);
    }

    public GetResponse() {
    }

    public boolean isAlreadySaved() {
        return alreadySaved;
    }

    public void setAlreadySaved(boolean alreadySaved) {
        this.alreadySaved = alreadySaved;
    }

    @Override
    public GetResponse error(String message) {
        return new GetResponse(message);
    }
}
