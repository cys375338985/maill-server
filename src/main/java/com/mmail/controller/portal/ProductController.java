package com.mmail.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmail.common.ServerResponse;
import com.mmail.service.IProductService;
import com.mmail.vo.ProductDetailvo;
import org.omg.CORBA.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by cys on 2018/5/31.
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductService productService;
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailvo> detail(Integer productId){
          return   productService.getProductDetail(productId);

    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false) String keyword,
                                                    @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                                    @RequestParam(value = "pageNum") int pageNum,
                                                    @RequestParam(value = "pageSize") int pageSize,
                                                    @RequestParam(value = "orderBy") String orderBy

    ){
        return   productService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);

    }


}
