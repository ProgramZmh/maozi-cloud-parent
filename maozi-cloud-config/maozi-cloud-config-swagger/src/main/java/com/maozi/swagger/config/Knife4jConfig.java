/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.maozi.swagger.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.maozi.sso.config.ApiWhitelistProperties;
import com.maozi.tool.ApplicationEnvironmentConfig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.Resource;

@Configuration
public class Knife4jConfig {
	
	private static final String headerName = "Authorization";
	
	@Resource
	private ApiWhitelistProperties apiWhitelist;

	@Bean
	public OpenAPI customOpenAPI() {

	Components components = new Components();
		 
	SecurityScheme securityScheme = new SecurityScheme();
	
	securityScheme.addExtension(headerName, "{{token}}");
	
	components.addSecuritySchemes(headerName,securityScheme
	
		.type(SecurityScheme.Type.APIKEY)
	    .scheme("Bearer")
	    .name(headerName)
	    .in(SecurityScheme.In.HEADER)
	    .description("请求头")
	    
	 );
		 
		 return new OpenAPI()
				 .components(components)
				 .info(new Info()
						 .title(ApplicationEnvironmentConfig.title)
						 .version(ApplicationEnvironmentConfig.version)
	                     .description(ApplicationEnvironmentConfig.details)
	                     .termsOfService("http://maozi.com")
	                     .license(new License().name("Apache 2.0").url("http://maozi.com")));
	 }
	 
	 @Bean
	 public GroupedOpenApi group() {
		 
		 List<String> apiWhitelistAll = Lists.newArrayList();
	    	
		 apiWhitelistAll.addAll(apiWhitelist.getWhitelist());
	    	
		 apiWhitelistAll.addAll(apiWhitelist.getDefaultWitelist());
		 
		 return GroupedOpenApi.builder().group(ApplicationEnvironmentConfig.applicationName)
				 
				 .pathsToExclude(apiWhitelistAll.toArray(new String [apiWhitelistAll.size()]))
				 
				 .addOperationCustomizer((operation, handlerMethod) -> {
					 
					 operation.addSecurityItem(new SecurityRequirement().addList(headerName,"{{token}}"));
								 
					 return operation;
					 
	              })
	              .build();
		 
	 }
	 

}