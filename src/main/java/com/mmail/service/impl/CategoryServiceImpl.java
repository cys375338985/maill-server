package com.mmail.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmail.common.ServerResponse;
import com.mmail.dao.CategoryMapper;
import com.mmail.pojo.Category;
import com.mmail.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cys on 2018/5/21.
 */
@Service("categoryService")
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
   @Autowired
   private CategoryMapper categoryMapper;
    @Override
    public ServerResponse addCategory(String catname, Integer parentId) {
        if(parentId == null || StringUtils.isBlank(catname)){
            return  ServerResponse.createByErrorMessage("添加商品品类参数错误");
        }
        Category cat = new Category();
        cat.setName(catname);
        cat.setParentId(parentId);
        cat.setStatus(true);

        int rowCont =categoryMapper.insert(cat);
        if(rowCont > 0){
            return ServerResponse.createBySuccess("添加品类成功");
        }

        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    @Override
    public ServerResponse updateCategoryName(Integer catid, String catname) {
        if(catid == null || StringUtils.isBlank(catname)){
            return  ServerResponse.createByErrorMessage("更新商品品类参数错误");
        }
        Category cat = new Category();
        cat.setId(catid);
        cat.setName(catname);

        int rowCont = categoryMapper.updateByPrimaryKeySelective(cat);

        if(rowCont > 0){
            return ServerResponse.createBySuccess("更新品类成功");
        }

        return ServerResponse.createByErrorMessage("更新品类失败");

    }

    @Override
    public ServerResponse getChildernParllelCategory(Integer categoryId) {

        List<Category> catlist =  categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(catlist)){
            logger.info("没有找到当前分类的子分类");
        }

        return ServerResponse.createBySuccess(catlist);
    }

    @Override
    public ServerResponse getCategoryAndDeepChildrenCategory(Integer categoryId) {

        //ServerResponse serverResponse = selectCategoryAndDeepChildrenCategory
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categoryId,categorySet);
        List<Integer> catIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category category : categorySet){
                catIdList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categorySet);
    }

    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categoryId,categorySet);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    public Set<Category> findChildCategory(Integer categoryId,Set<Category> catset){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            catset.add(category);

        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildCategory(categoryItem.getId(),catset);

        }
        return catset;

    }
}
