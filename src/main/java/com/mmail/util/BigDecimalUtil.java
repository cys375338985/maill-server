package com.mmail.util;

import java.math.BigDecimal;

/**
 * Created by cys on 2018/6/19.
 */
public class BigDecimalUtil {
    private BigDecimalUtil(){}
    public  static BigDecimal  add(double... args){
        BigDecimal b1 = new BigDecimal("0");
        for(double d : args){
                b1 = b1.add(new BigDecimal(Double.toString(d)));
        }
        return b1;
    }
    public  static BigDecimal  sub(double v1, double... args){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        for(double d : args){
            b1 = b1.subtract(new BigDecimal(Double.toString(d)));
        }
        return b1;
    }
    public  static BigDecimal  mul(double v1,double... args){
        if(v1==0d){
            return new BigDecimal("0");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        for(double d : args){
            b1 = b1.multiply(new BigDecimal(Double.toString(d)));
        }
        return b1;
    }
    public  static BigDecimal  div(double v1,double... args){

        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        for(double d : args){
            b1 = b1.divide(new BigDecimal(Double.toString(d)),2,BigDecimal.ROUND_HALF_UP);
        }
        return b1;
    }

}
