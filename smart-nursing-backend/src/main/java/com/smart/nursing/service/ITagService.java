package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.entity.TagEntity;

/**
 * 护理内容标签 Service 接口
 */
public interface ITagService extends IService<TagEntity> {

    /**
     * 获取全部标签
     *
     * @return 标签列表
     */
    java.util.List<TagEntity> getAllTags();

    /**
     * 分页条件查询标签
     *
     * @param page    分页参数
     * @param tagName 标签名称（模糊查询）
     * @return 分页结果
     */
    IPage<TagEntity> listTagByCondition(Page<TagEntity> page, String tagName);

    /**
     * 新增标签
     *
     * @param entity 标签信息
     */
    void addTag(TagEntity entity);

    /**
     * 修改标签
     *
     * @param entity 标签信息
     */
    void updateTag(TagEntity entity);

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     */
    void deleteTag(Long tagId);
}
