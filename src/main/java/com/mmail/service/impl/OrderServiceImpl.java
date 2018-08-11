package com.mmail.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.*;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmail.common.Const;
import com.mmail.common.ServerResponse;
import com.mmail.dao.*;
import com.mmail.pojo.*;
import com.mmail.pojo.OrderItem;
import com.mmail.service.IOrderService;
import com.mmail.util.BigDecimalUtil;
import com.mmail.util.DateTimeUtil;
import com.mmail.util.FtpUtil;
import com.mmail.util.PropertiesUtil;
import com.mmail.vo.OrderItemVo;
import com.mmail.vo.OrderProductVo;
import com.mmail.vo.OrderVo;
import com.mmail.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;


/**
 * Created by cys on 2018/7/2.
 */
@Service
public class OrderServiceImpl  implements IOrderService{
     private static   AlipayTradeService tradeService;
    static {
        Configs.init("zfbinfo.properties");
        tradeService= new AlipayTradeServiceImpl
                .ClientBuilder().build();
    }



    private  static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
   private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private  ShippingMapper shippingMapper;

    @Override
    public ServerResponse pay(Integer userId, String path, Long orderNo) {
        Map<String,String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return  ServerResponse.createByErrorMessage("用户没有订单");
        }


        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject =new StringBuilder()
                .append("可爱的商城扫码支付，订单号")
                .append(order.getOrderNo()).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单")
                .append(outTradeNo)
                .append(totalAmount)
                .append("元")
                .toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "可爱的商城";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "可爱的商城";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNoAndUserId(orderNo,userId);
        for (OrderItem orderItem: orderItems){

            GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),

                    BigDecimalUtil.mul( orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue()
                    ,orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods1);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

      AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
      switch (result.getTradeStatus()) {
          case SUCCESS:
              log.info("支付宝预下单成功: )");

              AlipayTradePrecreateResponse response = result.getResponse();
              dumpResponse(response);
              File folder = new File(path);
              if(!folder.exists()){
                  folder.setWritable(true);
                  folder.mkdirs();

              }
              // 需要修改为运行机器上的路径

              String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
              File targetFIle = new File(path,qrFileName);
              String qrPath   =  targetFIle.getAbsolutePath();
              ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
              try {
                  FtpUtil.uploadFile(Lists.newArrayList(targetFIle));
              } catch (Exception e) {
                  log.error("上传二维码异常",e);
              }
              log.info("filePath:" + qrPath);
              String qrurl=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFIle.getName();
              resultMap.put("qrurl",qrurl);
              return ServerResponse.createBySuccess(resultMap);
          case FAILED:
              log.error("支付宝预下单失败!!!");
              return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

          case UNKNOWN:
              log.error("系统异常，预下单状态未知!!!");
              return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

          default:
              log.error("不支持的交易状态，交易返回异常!!!");
              return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");

      }
    }

    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    @Override
    public ServerResponse aliCallBack(Map<String, String> map) {
        Long orderNo = Long.parseLong(map.get("out_trade_no"));
        String tradeNo = map.get("trade_no");
        String tradeStatus = map.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order==null){
            return ServerResponse.createByErrorMessage("订单号错误");
        }
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess("支付宝重复调用");
        }
        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(map.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order==null){
            return ServerResponse.createByErrorMessage("用户没有给订单");
        }
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }


    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        List<Cart> cartList = cartMapper.selectCartByCheckdeCart(userId);
        ServerResponse serverResponse = getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        if(CollectionUtils.isEmpty(orderItemList)){
            return  ServerResponse.createByErrorMessage("购物车为空");
        }
        BigDecimal payment = getOrderTotalPrice(orderItemList);

        Order  order = assembelOrder(userId,shippingId,payment);
        if(order  == null ){
            return  ServerResponse.createByErrorMessage("生成订单错误");
        }

        for(OrderItem orderItem: orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        orderItemMapper.batchInsert(orderItemList);

        reduecProductStrok(orderItemList);


        cleanCart(cartList);

        OrderVo orderVo = assembelOrderVo(order,orderItemList);

        return ServerResponse.createBySuccess(orderVo);

    }

    @Override
    public ServerResponse cancelOrder(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null ){
            return  ServerResponse.createByErrorMessage("该用户不存在此订单");
        }
        if(order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()){
            return  ServerResponse.createByErrorMessage("已付款,无法取消订单");
        }
        Order upoder = new Order();
        upoder.setId(order.getId());
        upoder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int row = orderMapper.updateByPrimaryKeySelective(upoder);
        if(row > 0){
            return  ServerResponse.createBySuccess();

        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId) {
        OrderProductVo orderProductVo= new OrderProductVo();
        List<Cart> cartList = cartMapper.selectCartByCheckdeCart(userId);
        ServerResponse serverResponse = getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem: orderItemList) {
            payment= BigDecimalUtil.add(payment.doubleValue()
                    ,orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembelOrderItemVo(orderItem));

        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    public ServerResponse deteil(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order!=null){
            List<OrderItem> orderItemList =
                    orderItemMapper.selectByOrderNoAndUserId(orderNo,userId);
            OrderVo orderVo = assembelOrderVo(order,orderItemList);
            return  ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("没有找到订单");
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId,
                                         int pageNum,
                                          int pageSize) {
        PageHelper.startPage(pageNum,pageSize);

       List<Order> orderList =   orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assemOrderVoList(userId,orderList);
        PageInfo page = new PageInfo(orderList);
        page.setList(orderVoList);
        return ServerResponse.createBySuccess(page);
    }

    @Override
    public ServerResponse<PageInfo> managelist(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList =   orderMapper.selectAll();
        List<OrderVo> orderVoList = assemOrderVoList(null,orderList);
        PageInfo page = new PageInfo(orderList);
        page.setList(orderVoList);
        return ServerResponse.createBySuccess(page);
    }

    @Override
    public ServerResponse<OrderVo> manageDetail(long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order !=null){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembelOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Override
    public ServerResponse<PageInfo> manageSerach(long orderNo, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order !=null){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembelOrderVo(order,orderItemList);
            PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
            pageInfo.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageInfo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Override
    public ServerResponse<String> manageSendGoods(long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            if(order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccess("已发货");
            }
            return ServerResponse.createByErrorMessage("当前订单没有付款或已发货");
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    private List<OrderVo> assemOrderVoList(Integer userId, List<Order> orderList) {
        List<OrderVo> orderVoList = Lists.newArrayList();

       orderList.forEach(order->{
           List<OrderItem> orderItemList= Lists.newArrayList();
           if(userId == null){
                 orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
           }else {
               orderItemList = orderItemMapper
                       .selectByOrderNoAndUserId(order.getOrderNo(),userId);

           }
           OrderVo orderVo = assembelOrderVo(order,orderItemList);
           orderVoList.add(orderVo);
       });
        return orderVoList;

    }


    /*
                *
                        ⎛⎝≥⏝⏝≤⎠⎞⎛⎝0⏝⏝0⎠⎞
                *
                * */
    private OrderVo assembelOrderVo(Order order, List<OrderItem> orderItemList) {
            OrderVo orderVo = new OrderVo();
            orderVo.setOrderNo(order.getOrderNo());
            orderVo.setPayment(order.getPayment());
            orderVo.setPaymentType(order.getPaymentType());
            orderVo.setPaymentTypeDesc(
                    Const.PaymentTypeEnum.codeof(order.getPaymentType()).getValue());
            orderVo.setPostage(order.getPostage());
            orderVo.setStatus(order.getStatus());
            orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
            Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
            if(shipping != null){
                orderVo.setReceiverName(shipping.getReceiverName());
                ShippingVo shippingVo = assEmbelShippingVO(shipping);
                orderVo.setShippingVo(shippingVo);
            }
            orderVo.setPaymentTime(DateTimeUtil.dateTostr(order.getPaymentTime()));
            orderVo.setSendTime(DateTimeUtil.dateTostr(order.getSendTime()));
            orderVo.setEndTime(DateTimeUtil.dateTostr(order.getEndTime()));
            orderVo.setCreateTime(DateTimeUtil.dateTostr(order.getCreateTime()));
            orderVo.setCloseTime(DateTimeUtil.dateTostr(order.getCloseTime()));
            orderVo.setImgaeHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
            List<OrderItemVo> orderItemVoList = Lists.newArrayList();
            for (OrderItem orderItem: orderItemList){
                orderItemVoList.add(assembelOrderItemVo(orderItem));
            }
            orderVo.setOrderItemVoList(orderItemVoList);

            return  orderVo;

    }

    private OrderItemVo assembelOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(orderItem.getCreateTime());
        return  orderItemVo;

    }

    private ShippingVo assEmbelShippingVO(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;

    }

    private void cleanCart(List<Cart> cartList) {
        cartList.forEach(cart -> {
            cartMapper.deleteByPrimaryKey(cart.getId());
        });
    }

    private void reduecProductStrok(List<OrderItem> orderItemList) {
        orderItemList.forEach((item)->{
            Product product = productMapper.selectByPrimaryKey(item.getProductId());
            if(product!=null){
                product.setStock(product.getStock()-item.getQuantity());
            }
            productMapper.updateByPrimaryKeySelective(product);
        });
    }

    private Order assembelOrder(Integer userId, Integer shippingId, BigDecimal payment) {
            Order order = new Order();
            order.setOrderNo(generateOrderNO());
            order.setUserId(userId);
            order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
            order.setPostage(0);
            order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
            order.setShippingId(shippingId);
            order.setPayment(payment);
            int rowCount = orderMapper.insert(order);
            if(rowCount > 0 ){
                return  order;
            }
            return null;
    }

    private Long generateOrderNO() {
        long currentTime = System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
       for (OrderItem orderItem: orderItemList) {
           payment =  BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if(CollectionUtils.isEmpty(cartList)){
            return  ServerResponse.createByErrorMessage("购物车是空的");
        }
        for (Cart cartItem : cartList){
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if(Const.ProductStatus.ON_SALE.getCode() !=product.getStatus()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"不在售卖状态");

            }
            if (cartItem.getQuantity()>product.getStock()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"库存不足");
            }
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductName(product.getName());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);

    }
}
