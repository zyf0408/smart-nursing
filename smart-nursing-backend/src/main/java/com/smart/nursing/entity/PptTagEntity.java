package com.smart.nursing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 课件-标签关联实体
 */
@Data
@ToString
@TableName("nursing_ppt_tag")
public class PptTagEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课件ID
     */
    private Long pptId;

    /**
     * 标签ID
     */
    private Long tagId;
}
