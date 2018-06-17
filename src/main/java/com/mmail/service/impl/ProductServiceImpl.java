package com.mmail.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmail.common.Const;
import com.mmail.common.ResponseCode;
import com.mmail.common.ServerResponse;
import com.mmail.dao.CategoryMapper;
import com.mmail.dao.ProductMapper;
import com.mmail.pojo.Category;
import com.mmail.pojo.Product;
import com.mmail.service.ICategoryService;
import com.mmail.service.IProductService;
import com.mmail.util.DateTimeUtil;
import com.mmail.util.PropertiesUtil;
import com.mmail.vo.ProductDetailvo;
import com.mmail.vo.ProductListVo;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cys on 2018/5/23.
 */
@Service("productService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService categoryService;
    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product!=null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if(product.getId()!=null){
               int  rowCount = productMapper.updateByPrimaryKey(product);
               if(rowCount > 0 ){
                   return ServerResponse.createBySuccessMessage("更新产品成功");
               }else {
                   return ServerResponse.createByErrorMessage("更新产品失败");
               }

            }else {
                int  rowCount =  productMapper.insert(product);
                if(rowCount > 0){
                    return ServerResponse.createBySuccessMessage("新增产品成功");
                }else {
                    return ServerResponse.createBySuccessMessage("新增产品失败");
                }

            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    @Override
    public ServerResponse setSaleStatus(Integer id, Integer status) {
        if(id == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                    ,ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        int rowCount= productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0){
            return  ServerResponse.createBySuccessMessage("相关产品销售状态成功");
        }


        return ServerResponse.createByErrorMessage("相关产品销售状态成功");
    }


    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product product : productList) {
           productListVoList.add( assmbleProductListVo(product));
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }



    public ServerResponse<ProductDetailvo> manageProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                    ,ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或被移除");
        }
        ProductDetailvo productDetailvo = assembleProductDetailVo(product);
        return  ServerResponse.createBySuccess(productDetailvo);
    }

    @Override
    public ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList = new ArrayList<>();
        productMapper.selectByNameAndProductId(productName,productId);
        for (Product product : productList) {
            productListVoList.add( assmbleProductListVo(product));
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return null;
    }

    private ProductDetailvo assembleProductDetailVo(Product product) {
        ProductDetailvo productDetailvo = new ProductDetailvo();
        productDetailvo.setId(product.getId());
        productDetailvo.setSubTitle(product.getSubtitle());
        productDetailvo.setPrice(product.getPrice());
        productDetailvo.setMainImage(product.getMainImage());
        productDetailvo.setSubImages(product.getSubImages());
        productDetailvo.setCategoryId(product.getCategoryId());
        productDetailvo.setDetail(product.getDetail());
        productDetailvo.setName(product.getName());
        productDetailvo.setStock(product.getStock());

        productDetailvo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                "http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        productDetailvo.setParentCatgoryId(category.getParentId());
        productDetailvo.setParentCatgoryId(category == null ? product.getCategoryId() :category.getParentId());
        productDetailvo.setCreateTime(DateTimeUtil.dateTostr(product.getCreateTime()));
        productDetailvo.setUpdateTime(DateTimeUtil.dateTostr(product.getUpdateTime()));
        return productDetailvo;

    }
    private ProductListVo assmbleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                "http://img.happymmall.com/"));
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }


    @Override
    public ServerResponse<ProductDetailvo> getProductDetail(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                    ,ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或被移除");
        }
        if (product.getStatus() != Const.ProductStatus.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("产品已下架或被移除");
        }
        ProductDetailvo productDetailvo = assembleProductDetailVo(product);
        return  ServerResponse.createBySuccess(productDetailvo);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId,
                                                                int pageNum, int pageSize,String orderBy) {
        if(StringUtils.isBlank(keyword)&&categoryId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        }
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = categoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
                keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] sts= orderBy.split("_");
                PageHelper.orderBy(sts[0]+" "+sts[1]);
            }
        }
        List<Product> productList = productMapper.SelectByNameAndCategoryIds(
                StringUtils.isBlank(keyword) ? null : keyword
                ,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product :
                productList) {
            productListVoList.add(assmbleProductListVo(product));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
