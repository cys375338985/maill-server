package com.mmail.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmail.common.Const;
import com.mmail.common.ResponseCode;
import com.mmail.common.ServerResponse;
import com.mmail.dao.CartMapper;
import com.mmail.dao.CategoryMapper;
import com.mmail.dao.ProductMapper;
import com.mmail.pojo.Cart;
import com.mmail.pojo.Product;
import com.mmail.pojo.User;
import com.mmail.service.ICartService;
import com.mmail.util.BigDecimalUtil;
import com.mmail.util.PropertiesUtil;
import com.mmail.vo.CartProductVo;
import com.mmail.vo.Cartvo;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cys on 2018/6/18.
 */
@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<Cartvo> addCart(User user, Integer productId, Integer count) {
        Integer userid = user.getId();
        Cart cart =  cartMapper.selectCatByUserIdProductId(userid,productId);
        if(cart ==null){
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userid);
            cartMapper.insert(cartItem);
        }else {
            count += cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        Cartvo cartvo = this.getCartVoLimit(userid);
        return ServerResponse.createBySuccess(cartvo);
    }

    @Override
    public ServerResponse<Cartvo> updataCart(User user, Integer productId, Integer count) {
        Integer userid = user.getId();
        Cart cart =  cartMapper.selectCatByUserIdProductId(userid,productId);
        if(cart != null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }else {


        }
        Cartvo cartvo = this.getCartVoLimit(userid);
        return ServerResponse.createBySuccess(cartvo);
    }

    @Override
    public ServerResponse<Cartvo> dalectProduct(User user, String productids) {
        Integer userid = user.getId();
       // List<String>  list =  Arrays.asList(productids.split(","));
        List<String> priductIdList = Splitter.on(",").splitToList(productids);
        if(CollectionUtils.isEmpty(priductIdList)){
            ResponseCode responseCode  =  ResponseCode.ILLEGAL_ARGUMENT;
            return ServerResponse
                    .createByErrorCodeMessage(responseCode.getCode(),responseCode.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userid,priductIdList);
        Cartvo cartvo = this.getCartVoLimit(userid);
        return ServerResponse.createBySuccess(cartvo);
    }

    private Cartvo getCartVoLimit(Integer userId){
        Cartvo cartvo = new Cartvo();
        List<Cart> cartList =  cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)){
            for (Cart cart: cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setProductId(cart.getProductId());
                cartProductVo.setUserid(userId);
                cartProductVo.setQuantity(cart.getQuantity());
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(product != null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductSudtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    int buyLimitCount = 0;
                    if(product.getStock()>= cart.getQuantity()){
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.
                                setLimtQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        buyLimitCount = product.getStock();
                        cartProductVo.
                                setLimtQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKey(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setProductTotalPrice(
                            BigDecimalUtil.mul(product.getPrice().doubleValue()
                            ,cart.getQuantity()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                if(cart.getChecked()==Const.Cart.CHECKED){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),
                            cartProductVo.getProductTotalPrice().doubleValue());

                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartvo.setCartToalPrice(cartTotalPrice);
        cartvo.setCartProductVoList(cartProductVoList);
        cartvo.setAllChecked(this.getAllCheckedStatus(userId));
        cartvo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return  cartvo;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        int i = cartMapper.selectCartProuductChenckedStatusByUserId(userId);
        return i==0;
    }

    @Override
    public ServerResponse<Cartvo> getList(User user) {
        Cartvo cartvo = this.getCartVoLimit(user.getId());
        return ServerResponse.createBySuccess(cartvo);
    }

    @Override
    public ServerResponse<Cartvo> selectOrUnselect(User user,Integer checked,Integer productId) {

        int i =cartMapper.checkedOrUmcheckedProduct(user.getId(),checked,productId);
        return getList(user);
    }

    @Override
    public ServerResponse<Integer> getCartCount(User user) {
        if(user == null){
            return ServerResponse.createBySuccess(0);
        }
        int count = cartMapper.selectCartProuductCount(user.getId());
        return ServerResponse.createBySuccess(count);
    }
}
