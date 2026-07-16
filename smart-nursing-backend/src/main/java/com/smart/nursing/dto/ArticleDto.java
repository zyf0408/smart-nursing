package com.smart.nursing.dto;

import com.smart.nursing.entity.ArticleEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 文章查询/保存 DTO（继承文章实体全部字段，并携带分页参数与标签ID集合）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleDto extends ArticleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Integer pageNo = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 标签ID集合
     */
    private List<Long> tagIds;
}
