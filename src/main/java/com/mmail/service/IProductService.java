package com.mmail.service;

import com.github.pagehelper.PageInfo;
import com.mmail.common.ServerResponse;
import com.mmail.pojo.Product;
import com.mmail.vo.ProductDetailvo;

/**
 * Created by cys on 2018/5/23.
 */
public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer id, Integer status);

     ServerResponse<ProductDetailvo> manageProductDetail(Integer productId);

    ServerResponse getProductList(int pageNum, int pageSize);

    ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse<ProductDetailvo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId,
                                                         int pageNum, int pageSize,String orderBy);

}
