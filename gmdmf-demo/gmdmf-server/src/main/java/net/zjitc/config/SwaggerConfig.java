package net.zjitc.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    // http://localhost:8080/doc.html
    // http://localhost:8080/swagger-ui/index.html
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                // 接口文档标题
                .info(new Info().title("工贸大马蜂")
                        // 文档简介
                        .description("工贸大马蜂官方API文档")
                        // 文档版本
                        .version("v2.0")
                        // 开发者联系方式
                        .contact(new Contact().name("Axier工作室")
                                .email("13906631248@163.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("官网")
                        .url("https://www.zjitc.net/"));
    }
}


