package net.zjitc.config;

import lombok.RequiredArgsConstructor;
import net.zjitc.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 调用注册器 registry
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/login/**")
                .excludePathPatterns("/**/login")
                .excludePathPatterns("**/register")
                .excludePathPatterns("/course/findBy")
                .excludePathPatterns("/major")
                .excludePathPatterns("/department/schoolId")
                .excludePathPatterns("/department/schoolName")
                .excludePathPatterns("/school")
                .excludePathPatterns("/course/findBy")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/v3/**")
                .excludePathPatterns("/**/doc.html")
                .excludePathPatterns("/swagger-ui/**");  // 不拦截 swagger
    }
}
