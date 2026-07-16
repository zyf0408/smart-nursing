package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.common.enums.GlobalErrorCodeConstants;
import com.smart.nursing.common.exception.BusinessException;
import com.smart.nursing.dao.ArticleMapper;
import com.smart.nursing.dao.CategoryMapper;
import com.smart.nursing.entity.ArticleEntity;
import com.smart.nursing.entity.CategoryEntity;
import com.smart.nursing.service.ICategoryService;
import com.smart.nursing.vo.CategoryTreeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 护理内容分类 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements ICategoryService {

    private final ArticleMapper articleMapper;

    @Override
    public List<CategoryTreeVo> getCategoryTree() {
        // 查全部类别
        List<CategoryEntity> allCategories = this.list(new LambdaQueryWrapper<CategoryEntity>()
                .orderByAsc(CategoryEntity::getSortOrder));
        // 递归构建树
        return buildCategoryTree(allCategories, 0L);
    }

    @Override
    public void addCategory(CategoryEntity entity) {
        if (entity.getParentId() == null) {
            entity.setParentId(0L);
        }
        if (entity.getSortOrder() == null) {
            entity.setSortOrder(0);
        }
        this.save(entity);
    }

    @Override
    public void updateCategory(CategoryEntity entity) {
        this.updateById(entity);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        // 检查是否存在子分类
        long childCount = this.count(new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getParentId, categoryId));
        if (childCount > 0) {
            throw new BusinessException(GlobalErrorCodeConstants.CATEGORY_HAS_CHILDREN);
        }
        // 检查分类下是否存在内容
        long contentCount = articleMapper.selectCount(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getCategoryId, categoryId));
        if (contentCount > 0) {
            throw new BusinessException(GlobalErrorCodeConstants.CATEGORY_HAS_CONTENT);
        }
        this.removeById(categoryId);
    }

    /**
     * 递归构建分类树
     *
     * @param categories 分类列表
     * @param parentId   父级ID
     * @return 分类树
     */
    private List<CategoryTreeVo> buildCategoryTree(List<CategoryEntity> categories, Long parentId) {
        List<CategoryTreeVo> tree = new ArrayList<>();
        for (CategoryEntity category : categories) {
            Long pid = category.getParentId() == null ? 0L : category.getParentId();
            if (pid.equals(parentId)) {
                CategoryTreeVo vo = new CategoryTreeVo();
                BeanUtils.copyProperties(category, vo);
                vo.setChildren(buildCategoryTree(categories, category.getCategoryId()));
                tree.add(vo);
            }
        }
        return tree;
    }
}
