package net.zjitc.interceptor;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.utils.JwtUtil;
import net.zjitc.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取令牌
        String token = request.getHeader("Authorization");
        try {
            // 查看令牌是否有效
            if (StrUtil.isBlank(stringRedisTemplate.opsForValue().get(token))) {
                throw new RuntimeException("登录缓存失效，请重新登录");
            }
            // 验证 token
            Map<String, Object> map = JwtUtil.parseToken(token);
            // 使用 ThreadLocal 存储数据，在多个层中共享数据
            ThreadLocalUtil.set(map);
            return true;    // 放行
        } catch (Exception e) {
            response.setStatus(Code.NOT_LOGIN);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();   // 在使用完后清空，防止内存泄漏
    }
}
