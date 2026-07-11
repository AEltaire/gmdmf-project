package net.zjitc.controller;

import net.zjitc.constant.Code;
import net.zjitc.result.Result;

public class BaseController {

    protected <T> Result<T> toGetAjax(boolean flag) {
        return flag ? Result.success(Code.GET_OK, "操作成功") : Result.error(Code.GET_ERR, "系统繁忙，请稍后再试");
    }

    protected <T> Result<T> toPostAjax(boolean flag) {
        return flag ? Result.success(Code.POST_OK, "操作成功") : Result.error(Code.POST_ERR, "系统繁忙，请稍后再试");
    }

    protected <T> Result<T> toPutAjax(boolean flag) {
        return flag ? Result.error(Code.PUT_ERR, "系统繁忙，请稍后再试") : Result.success(Code.PUT_OK, "操作成功");
    }

    protected <T> Result<T> toDeleteAjax(boolean flag) {
        return flag ? Result.success(Code.DELETE_OK, "操作成功") : Result.error(Code.DELETE_ERR, "系统繁忙，请稍后再试");
    }

    protected <T> Result<T> toGetAjax(boolean flag, String message) {
        return flag ? Result.success(Code.GET_OK, message) : Result.error(Code.GET_ERR, "系统繁忙，请稍后再试");
    }

    protected <T> Result<T> toPostAjax(boolean flag, String message) {
        return flag ? Result.success(Code.POST_OK, message) : Result.error(Code.POST_ERR, "系统繁忙，请稍后再试");
    }

    protected <T> Result<T> toPutAjax(boolean flag, String message) {
        return flag ? Result.success(Code.PUT_OK, message) : Result.error(Code.PUT_ERR, "系统繁忙，请稍后再试");
    }

    protected <T> Result<T> toDeleteAjax(boolean flag, String message) {
        return flag ? Result.success(Code.DELETE_OK, message) : Result.error(Code.DELETE_ERR, "系统繁忙，请稍后再试");
    }

    protected <T> Result<T> toGetAjax(boolean flag, String message, T data) {
        return flag ? Result.success(Code.GET_OK, "查询记录为空") : Result.success(Code.GET_OK, message, data);
    }

    protected <T> Result<T> toPostAjax(boolean flag, String message, T data) {
        return flag ? Result.success(Code.POST_OK, "查询记录为空") : Result.success(Code.POST_OK, message, data);
    }
}
