package com.dz.dsl.extension;

import com.dz.dsl.GeneralResponse;

/**
 * Response of saving a stackoverflow.com answer
 *
 * @author mamad
 * @since 23/11/14.
 */
public class SaveSOFAnswerResponse extends GeneralResponse<SaveSOFAnswerResponse> {
    private String url;

    public SaveSOFAnswerResponse(String errorMessage) {
        super(errorMessage);
    }

    public SaveSOFAnswerResponse() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public SaveSOFAnswerResponse error(String message) {
        return new SaveSOFAnswerResponse(message);
    }
}