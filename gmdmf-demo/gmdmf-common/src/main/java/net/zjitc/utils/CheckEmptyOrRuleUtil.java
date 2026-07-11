package net.zjitc.utils;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.zjitc.constant.Code;
import net.zjitc.exception.BusinessException;

import java.util.List;

public class CheckEmptyOrRuleUtil {

    public static boolean isEmptyList(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isEmptyPage(Page<?> page) {
        return page.getRecords() == null || page.getRecords().isEmpty();
    }

    public static boolean isEmptyString(String string) {
        return StrUtil.isBlank(string);
    }

    public static void isEmptyId(Long id) {
        if (id == null) {
            throw new BusinessException(Code.BUSINESS_ERR, "数据异常，请稍后再试");
        }
    }

    public static void isStudent() {
        if (!"学生".equals(ThreadLocalUtil.getRule()))
            throw new BusinessException(Code.BUSINESS_ERR, "非学生用户不能操作");
    }

    public static void isTeacher() {
        if (!"教师".equals(ThreadLocalUtil.getRule()))
            throw new BusinessException(Code.BUSINESS_ERR, "非教师用户不能操作");
    }

    public static void isManager() {
        if (!"管理".equals(ThreadLocalUtil.getRule())) throw new BusinessException(Code.BUSINESS_ERR, "仅限管理员操作");
    }

    public static void isTeacherOrManager() {
        if ("学生".equals(ThreadLocalUtil.getRule())) throw new BusinessException(Code.BUSINESS_ERR, "仅限教师和管理员操作");
    }
}
