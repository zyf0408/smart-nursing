package com.smart.nursing.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单树形结构 VO
 */
@Data
public class MenuTreeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 父级菜单ID
     */
    private Long parentId;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 是否可见（0-隐藏 1-显示）
     */
    private Integer visible;

    /**
     * 子菜单列表
     */
    private List<MenuTreeVo> children;
}
