/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.maozi.swagger.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import com.google.common.collect.Lists;
import com.maozi.tool.ApplicationEnvironmentConfig;

import cn.hutool.core.collection.CollectionUtil;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration 
@EnableSwagger2WebMvc
@Import(BeanValidatorPluginsConfiguration.class)
public class Knife4jConfig {

    private final OpenApiExtensionResolver openApiExtensionResolver;

    public Knife4jConfig(OpenApiExtensionResolver openApiExtensionResolver) {
        this.openApiExtensionResolver = openApiExtensionResolver;
    }

    @Bean(value = "defaultApi1")
    public Docket defaultApi1() {
    	
        List<SecurityScheme> securitySchemes=Arrays.asList(new ApiKey("Authorization", "Authorization", "header"));

        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;

        List<SecurityContext> securityContexts=Arrays.asList(SecurityContext.builder()
                .securityReferences(CollectionUtil.newArrayList(new SecurityReference("Authorization", authorizationScopes)))
                .build());

        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.maozi"))
                .paths(PathSelectors.any())
                .build()
                .extensions(openApiExtensionResolver.buildSettingExtensions())
            .securityContexts(securityContexts).securitySchemes(securitySchemes);
        
        List<ResponseMessage> responseMessageList = Lists.newArrayList(
                
        		new ResponseMessageBuilder().code(200).message("成功结果集").responseModel(new ModelRef("接口请求成功")).build(),
        		new ResponseMessageBuilder().code(400).message("参数错误 / 业务错误").responseModel(new ModelRef("接口错误结果集")).build(),
        		new ResponseMessageBuilder().code(401).message("Token过期错误 / 未携带Token").responseModel(new ModelRef("接口错误结果集")).build(),
        		new ResponseMessageBuilder().code(403).message("权限不足").responseModel(new ModelRef("接口错误结果集")).build(),
        		new ResponseMessageBuilder().code(404).message("数据不存在 / 服务器未部署").responseModel(new ModelRef("接口错误结果集")).build(),
        		new ResponseMessageBuilder().code(415).message("请求格式错误 如：Get携带Body").responseModel(new ModelRef("接口错误结果集")).build(),
        		new ResponseMessageBuilder().code(500).message("内部服务错误").responseModel(new ModelRef("接口错误结果集")).build()
        		
        );
        
        docket.globalResponseMessage(RequestMethod.GET, responseMessageList);
        docket.globalResponseMessage(RequestMethod.POST, responseMessageList);
        docket.globalResponseMessage(RequestMethod.PUT, responseMessageList);
        docket.globalResponseMessage(RequestMethod.DELETE, responseMessageList);

    	
    	ParameterBuilder parameterBuilder = new ParameterBuilder();
        List<Parameter> parameterList = new ArrayList<Parameter>();
        parameterBuilder.name("Authorization").description("通行令牌").modelRef(new ModelRef("String")).parameterType("header").required(false).build();
        parameterList.add(parameterBuilder.build());
        docket.globalOperationParameters(parameterList);
        
        docket.directModelSubstitute(Long.class, String.class);
        
        return docket;
        
    }
    
//    @Bean(value = "defaultApi1")
//    public Docket defaultApi1() {
//        List<GrantType> grantTypes=new ArrayList<>();
//        //密码模式
//       ResourceOwnerPasswordCredentialsGrant resourceOwnerPasswordCredentialsGrant=new ResourceOwnerPasswordCredentialsGrant(passwordTokenUrl);
//       grantTypes.add(resourceOwnerPasswordCredentialsGrant);
//       OAuth oAuth=new OAuthBuilder().name("oauth2").grantTypes(grantTypes).build();
//       //context
//       //scope方位
//       List<AuthorizationScope> scopes=new ArrayList<>();
//       scopes.add(new AuthorizationScope("backend","后端资源"));
//
//       SecurityReference securityReference=new SecurityReference("oauth2",scopes.toArray(new AuthorizationScope[]{}));
//       SecurityContext securityContext=new SecurityContext(Lists.newArrayList(securityReference),PathSelectors.ant("/**"));
//       //schemas
//       List<SecurityScheme> securitySchemes=Lists.newArrayList(oAuth);
//       //securyContext
//       List<SecurityContext> securityContexts=Lists.newArrayList(securityContext);
//       return new Docket(DocumentationType.SWAGGER_2)
//               .select()
//               .apis(RequestHandlerSelectors.basePackage("com"))
//               .paths(PathSelectors.any())
//               .build()
//               .securityContexts(securityContexts)
//               .securitySchemes(securitySchemes)
//               .apiInfo(apiInfo());
//    }
   
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title(ApplicationEnvironmentConfig.applicationName)
                .description(ApplicationEnvironmentConfig.applicationName+"接口文档")
                .termsOfServiceUrl("https://www.maozi.tech")
                .contact(new Contact("龙宝","https://www.maozi.tech","1095071913@qq.com"))
                .version("1.0")
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("BearerToken", "Authorization", "header");
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return CollectionUtil.newArrayList(new SecurityReference("BearerToken", authorizationScopes));
    }
    
    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier, ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier, EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties, WebEndpointProperties webEndpointProperties, Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping, null);
    }
    
    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
        return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
    }
    
    
}
///*
// * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
// * All rights reserved.
// * Official Web Site: http://www.xiaominfo.com.
// * Developer Web Site: http://open.xiaominfo.com.
// */
//
//package com.maozi.swagger.config;
//
//import java.util.Arrays;
//import java.util.List;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
//import com.maozi.factory.BaseResultFactory;
//import cn.hutool.core.collection.CollectionUtil;
//import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.oas.annotations.EnableOpenApi;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.ApiKey;
//import springfox.documentation.service.AuthorizationScope;
//import springfox.documentation.service.Contact;
//import springfox.documentation.service.SecurityReference;
//import springfox.documentation.service.SecurityScheme;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.contexts.SecurityContext;
//import springfox.documentation.spring.web.plugins.Docket;
//
///**
// * @author <a href="mailto:xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
// * 2020/09/18 11:04
// * @since:knife4j-spring-boot2-demo 1.0
// */
//@EnableOpenApi
//@Configuration 
//@Import(BeanValidatorPluginsConfiguration.class)
//public class Knife4jConfig {
//
//    private final OpenApiExtensionResolver openApiExtensionResolver;
//
//    public Knife4jConfig(OpenApiExtensionResolver openApiExtensionResolver) {
//        this.openApiExtensionResolver = openApiExtensionResolver;
//    }
//
//    @Bean(value = "defaultApi1")
//    public Docket defaultApi1() {
//        List<SecurityScheme> securitySchemes=Arrays.asList(new ApiKey("Authorization", "Authorization", "header"));
//
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//
//        List<SecurityContext> securityContexts=Arrays.asList(SecurityContext.builder()
//                .securityReferences(CollectionUtil.newArrayList(new SecurityReference("Authorization", authorizationScopes)))
//                .forPaths(PathSelectors.regex("/.*"))
//                .build());
//
//        Docket docket=new Docket(DocumentationType.OAS_30)
//                .apiInfo(apiInfo())
//                .groupName(BaseResultFactory.applicationName)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com"))
//                .paths(PathSelectors.any())
//                .build()
//                .extensions(openApiExtensionResolver.buildSettingExtensions())
//            .securityContexts(securityContexts).securitySchemes(securitySchemes);
//        return docket;
//    }
//   
//    private ApiInfo apiInfo(){
//        return new ApiInfoBuilder()
//                .title(BaseResultFactory.applicationName)
//                .description(BaseResultFactory.applicationName+"接口文档")
//                .termsOfServiceUrl("https://www.gdzskj.tech")
//                .contact(new Contact("龙宝","https://www.gdzskj.tech","maozi@maozi.info"))
//                .version("1.0")
//                .build();
//    }
//
//    private ApiKey apiKey() {
//        return new ApiKey("BearerToken", "Authorization", "header");
//    }
//    
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .forPaths(PathSelectors.regex("/.*"))
//                .build();
//    }
//
//    List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return CollectionUtil.newArrayList(new SecurityReference("BearerToken", authorizationScopes));
//    }
//    
//}

