package com.smart.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.entity.CategoryEntity;
import com.smart.nursing.vo.CategoryTreeVo;

import java.util.List;

/**
 * 护理内容分类 Service 接口
 */
public interface ICategoryService extends IService<CategoryEntity> {

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    List<CategoryTreeVo> getCategoryTree();

    /**
     * 新增分类
     *
     * @param entity 分类信息
     */
    void addCategory(CategoryEntity entity);

    /**
     * 修改分类
     *
     * @param entity 分类信息
     */
    void updateCategory(CategoryEntity entity);

    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);
}
