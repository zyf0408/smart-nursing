package com.smart.nursing.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 护理课件（PPT）实体
 */
@Data
@ToString
@TableName("nursing_ppt")
public class PptEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课件ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long pptId;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 描述
     */
    private String description;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态（0-下架 1-上架）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识（0-未删除 1-已删除）
     */
    @TableLogic
    private Integer isDelete;
}
