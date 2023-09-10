/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.maozi.bd.api.impl;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.maozi.bd.api.BDService;
import com.maozi.bd.properties.BDProperties;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.code.CodeAttribute;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.mvc.config.rest.RestTemplate;

import jakarta.annotation.Resource;

/**	
 * 
 *  Specifications：功能
 * 
 *  Author：彭晋龙
 * 
 *  Creation Date：2021-12-18:16:32:34
 *
 *  Copyright Ownership：xiao mao zi
 * 
 *  Agreement That：Apache 2.0
 * 
 */

public class BDServiceImpl extends BaseCommon implements BDService{

	@Resource
	protected BDProperties bdProperties;
	
	@Resource
	private RestTemplate restClient;
	
	@Override
	public JSONObject bdRest(String uri,Map<String,Object> privateParam,HttpMethod method) {
		
		if(isNull(privateParam)) {
			privateParam = Maps.newHashMap();
		}
		
		privateParam.put("ak", bdProperties.getAk());
		
		ResponseEntity<String> bdResult = null;
		if(HttpMethod.GET.equals(method)) {
			
			bdResult = restClient.getForEntity(bdProperties.getUrl()+uri+"&ak={ak}&output=json", String.class,privateParam);
			
		}else {
			
			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(privateParam, new HttpHeaders());
			bdResult = restClient.postForEntity(bdProperties.getUrl()+uri,requestEntity,String.class);
			
		}
		
		JSONObject response = JSONObject.parseObject(bdResult.getBody());
		
		if(isNull(bdResult)) {
			throw new BusinessResultException(new CodeAttribute(500,"百度服务不可用",500));
		}
		
		if(isNull(bdResult) || bdResult.getStatusCodeValue() != 200 || response.getInteger("status")!=0) { 
			throw new BusinessResultException(new CodeAttribute(response.getInteger("status"),response.getString("message")),bdResult.getStatusCodeValue());
		}
		
		return response; 
		
	}
	
}
