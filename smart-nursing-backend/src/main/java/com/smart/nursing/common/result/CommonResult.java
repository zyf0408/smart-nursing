package com.smart.nursing.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应封装
 *
 * @param <T> 数据类型
 */
@Data
public class CommonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功状态码
     */
    public static final Integer SUCCESS_CODE = 200;

    /**
     * 成功提示信息
     */
    public static final String SUCCESS_MSG = "success";

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 成功（无数据）
     */
    public static <T> CommonResult<T> success() {
        return success(null);
    }

    /**
     * 成功（带数据）
     */
    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.setCode(SUCCESS_CODE);
        result.setMsg(SUCCESS_MSG);
        result.setData(data);
        return result;
    }

    /**
     * 成功（分页数据）
     * <p>
     * 返回结构：records、total、pageNo、pageSize
     */
    public static <T> CommonResult<PageData<T>> successPageData(IPage<T> page) {
        PageData<T> pageData = new PageData<>();
        pageData.setRecords(page.getRecords());
        pageData.setTotal(page.getTotal());
        pageData.setPageNo((int) page.getCurrent());
        pageData.setPageSize((int) page.getSize());
        return success(pageData);
    }

    /**
     * 失败（自定义状态码与提示信息）
     */
    public static <T> CommonResult<T> error(Integer code, String msg) {
        CommonResult<T> result = new CommonResult<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败（基于错误码对象）
     */
    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }
}
