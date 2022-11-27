package com.zy7y.reggie_take_out.common;

/**
 * 封装工具类，保存和获取当前登录用户id， 一个请求过来即是一个线程
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void  setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
