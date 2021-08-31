package com.example.himalaya.base;

/**
 * @author: Liu
 * @date: 2021/8/26
 */
public interface IBasePresenter<T> {
    /**
     * 注册UI的回调接口
     * @param t
     */
    void registerViewCallback(T t);

    /**
     * 取消UI的回调接口
     * @param t
     */
    void unRegisterViewCallback(T t);
}
