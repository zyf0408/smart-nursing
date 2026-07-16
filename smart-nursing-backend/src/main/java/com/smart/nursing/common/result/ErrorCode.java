package com.smart.nursing.common.result;

import lombok.Getter;

/**
 * 错误码对象
 */
@Getter
public class ErrorCode {

    private final Integer code;
    private final String msg;

    public ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
