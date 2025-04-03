package com.water.backend.utils;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 *
 * @param <T> 数据泛型
 */
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     * 200 - 成功
     * 500 - 失败
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 私有构造方法
     */
    private R() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法
     *
     * @param code 状态码
     * @param msg  消息
     * @param data 数据
     */
    private R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功返回结果
     */
    public static <T> R<T> ok() {
        return new R<>(200, "操作成功", null);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     */
    public static <T> R<T> ok(T data) {
        return new R<>(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     *
     * @param msg  提示信息
     * @param data 返回数据
     */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(200, msg, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> R<T> fail() {
        return new R<>(500, "操作失败", null);
    }

    /**
     * 失败返回结果
     *
     * @param msg 提示信息
     */
    public static <T> R<T> fail(String msg) {
        return new R<>(500, msg, null);
    }

    /**
     * 失败返回结果
     *
     * @param code 状态码
     * @param msg  提示信息
     */
    public static <T> R<T> fail(Integer code, String msg) {
        return new R<>(code, msg, null);
    }

    /**
     * 设置状态码
     *
     * @param code 状态码
     */
    public R<T> code(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * 设置提示信息
     *
     * @param msg 提示信息
     */
    public R<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    // getter方法
    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}