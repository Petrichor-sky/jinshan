package com.haizhi.empower.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


/**
 * Swagger配置类
 *
 * @author lvchengfei
 * @since 1.0.0
 */
@EnableSwagger2WebMvc
@Configuration
public class Swagger2Config {

	@Value("${swagger.path}")
	private String path;
	@Bean
	public Docket creatRestApi() {
		Docket docket=new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				//这里指定Controller扫描包路径
				.apis(RequestHandlerSelectors.basePackage(path))
				.paths(PathSelectors.any())
				.build();
		return docket;
	}


	/**
	 * 文档描述信息
	 *
	 * @return
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.version("1.0")
				.contact(new Contact("lvchengfei", "http:localhost:8180/empower/doc.html", "kt_lvchengfei@163.com"))
				.description("金山赋能")
				.title("金山赋能")
				.build();
	}

}