package net.zjitc.utils;

import cn.hutool.core.convert.Convert;
import net.zjitc.constant.Code;
import net.zjitc.exception.BusinessException;

import java.util.Map;

@SuppressWarnings("all")    // 抑制所有警告
public class ThreadLocalUtil {

    // 提供ThreadLocal对象
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    // 根据键获取值
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }
	
    // 存储键值对
    public static void set(Object value){
        THREAD_LOCAL.set(value);
    }

    // 清除ThreadLocal 防止内存泄漏
    public static void remove(){
        THREAD_LOCAL.remove();
    }

    // 获取id
    public static Long getId() {
        try {
            Map<String, Object> map = get();
            return Convert.toLong(map.get("id"));
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "请先登录");
        }
    }

    // 获取 name
    public static String getName() {
        try {
            Map<String, Object> map = get();
            return Convert.toStr(map.get("name"));
        } catch (Exception e) {
            throw new BusinessException(Code.NOT_LOGIN, "请先登录");
        }
    }

    // 获取 username
    public static String getUsername() {
        try {
            Map<String, Object> map = get();
            return Convert.toStr(map.get("username"));
        } catch (Exception e) {
            throw new BusinessException(Code.NOT_LOGIN, "请先登录");
        }
    }

    // 获取权限
    public static String getRule() {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            return (String) map.get("rule");
        } catch (Exception e) {
            throw new BusinessException(Code.NOT_LOGIN, "请先登录");
        }
    }

    // 获取学校
    public static String getSchool() {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            return (String) map.get("school");
        } catch (Exception e) {
            throw new BusinessException(Code.NOT_LOGIN, "请先登录");
        }
    }
}
