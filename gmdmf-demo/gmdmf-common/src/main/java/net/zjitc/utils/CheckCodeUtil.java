package net.zjitc.utils;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;

public class CheckCodeUtil {

    public static String getCode()  {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 45, 4 ,40);
        return lineCaptcha.getCode();
    }

    public static Boolean checkCode(String code, String checkCode) {
        return checkCode.equalsIgnoreCase(code);
    }
}
