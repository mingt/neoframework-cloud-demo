
package com.anilallewar.microservices.auth.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * Created on 2018/5/24 0024.
 *
 * @author zlf
 * @author extended by ahming
 * @email i @merryyou.cn
 * @since 1.0
 */
@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {

    private String error;

    /**
     * Gets error.
     *
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * Sets error.
     *
     * @param error the error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * 不带 error .
     *
     * @param msg the msg
     */
    public CustomOauthException(String msg) {
        super(msg);
    }

    /**
     * 可带 error . 把原来类似以下的错误完全传递好. InvalidGrantException 等等:
     *
     * <p>{
     * "error": "invalid_request",
     * "error_description": "Missing grant type"
     * }</p>
     *
     * @param msg the msg
     * @param error the error
     */
    public CustomOauthException(String msg, String error) {
        super(msg);
        this.error = error;
    }

}
