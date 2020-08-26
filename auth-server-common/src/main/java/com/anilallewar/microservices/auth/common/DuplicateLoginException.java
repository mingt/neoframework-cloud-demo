
package com.anilallewar.microservices.auth.common;

import com.neoframework.common.api.exception.BaseApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 同端重复登录exception
 *
 * @author igylove
 * @create 2018-08-03 13:43
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class DuplicateLoginException extends BaseApiException {

    private static final Integer DUPLICATE_LOGIN_STATUS = 401999;
    private static final String DUPLICATE_LOGIN_MSG = "当前账户已经在其他设备上登录了,请先下线后再登录";

    public DuplicateLoginException(String msg, Throwable t) {
        super(msg, t);
    }

    public DuplicateLoginException(String msg) {
        this(DUPLICATE_LOGIN_STATUS, msg);
    }

    public DuplicateLoginException(Integer bizStatus, String msg) {
        super(bizStatus, msg);
    }

    public DuplicateLoginException() {
        this(DUPLICATE_LOGIN_STATUS, DUPLICATE_LOGIN_MSG);
    }
}
