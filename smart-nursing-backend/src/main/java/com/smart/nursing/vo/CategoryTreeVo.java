package com.smart.nursing.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分类树形结构 VO
 */
@Data
public class CategoryTreeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父级分类ID
     */
    private Long parentId;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 描述
     */
    private String description;

    /**
     * 子分类列表
     */
    private List<CategoryTreeVo> children;
}
