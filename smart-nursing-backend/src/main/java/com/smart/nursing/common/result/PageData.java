package com.smart.nursing.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果数据封装
 *
 * @param <T> 数据类型
 */
@Data
public class PageData<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private int pageNo;

    /**
     * 每页条数
     */
    private int pageSize;
}
