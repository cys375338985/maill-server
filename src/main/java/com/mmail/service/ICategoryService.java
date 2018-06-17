package com.mmail.service;

import com.mmail.common.ServerResponse;
import com.mmail.pojo.Category;

import java.util.List;

/**
 * Created by cys on 2018/5/21.
 */
public interface ICategoryService {
    ServerResponse addCategory(String catname, Integer parentId);

    ServerResponse updateCategoryName(Integer catid, String catname);

    ServerResponse getChildernParllelCategory(Integer categoryId);

    ServerResponse getCategoryAndDeepChildrenCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
