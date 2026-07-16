package com.smart.nursing.common.exception;

import com.smart.nursing.common.enums.GlobalErrorCodeConstants;
import com.smart.nursing.common.result.ErrorCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public BusinessException(GlobalErrorCodeConstants errorCodeConstants) {
        super(errorCodeConstants.getMsg());
        this.code = errorCodeConstants.getCode();
        this.message = errorCodeConstants.getMsg();
    }
}
