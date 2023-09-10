package com.maozi.base.api.impl.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.maozi.base.annotation.RestService;
import com.maozi.base.result.EnumResult;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.code.CodeAttribute;
import com.maozi.common.result.code.CodeHashMap;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.tool.ApplicationEnvironmentConfig;
import com.maozi.tool.MapperUtils;

import cn.hutool.core.util.ClassUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestService
@Tag(name = "【全局】服务信息")
public class RestApplication extends BaseCommon {

	private String dataId = "saas-code.json";

	private String group = "DEFAULT_GROUP";

	private Map<String, Object> enums = new HashMap<String, Object>();

	public RestApplication() throws Exception {
		
		
		ClassUtil.scanPackage("com.maozi.base.enums").stream()
		
			.forEach(item -> {
			
				if (item.isEnum()) {
				
					Object[] enumConstants = item.getEnumConstants(); 
				
					char[] charArray = item.getSimpleName().toCharArray(); 
				
					charArray[0] += 32;
				
					enums.put(new String(charArray), enumConstants);
				}
			
			}
		);
		
		ClassUtil.scanPackage("com.maozi."+ApplicationEnvironmentConfig.applicationProjectAbbreviation).stream()
		
				.forEach(item -> {
					
					if (item.isEnum()) {
						
						Object[] enumConstants = item.getEnumConstants(); 
						
						char[] charArray = item.getSimpleName().toCharArray(); 
						
						charArray[0] += 32;
						
						enums.put(new String(charArray), enumConstants);
					}
					
				}
		);
		
		ConfigService configService = NacosFactory.createConfigService(ApplicationEnvironmentConfig.nacosAddr);
		
		setCode(configService.getConfig(dataId, group, 5000));
		
		configService.addListener(dataId, group, new Listener() {
			
			@Override
			public void receiveConfigInfo(String codeJson) {
				setCode(codeJson);
			}

			@Override
			public Executor getExecutor() {
				return null;
			}
			
		});
		
	}

	public void setCode(String codeJson) {

		try {
			
			JSONObject codeMaps = JSONObject.parseObject(codeJson);
			Map<String, CodeHashMap> newCodeDatas = new HashMap<>();
			for(Object serviceCodeNameObject : codeMaps.keySet()) {
				
				String serviceCodeName=serviceCodeNameObject.toString();
				
				if(serviceCodeName.contains(ApplicationEnvironmentConfig.applicationName) && serviceCodeName.contains("enums")) {
					
					JSONObject serviceEnums = codeMaps.getJSONObject(serviceCodeName);
					for(Object serviceEnumKey:serviceEnums.keySet()) {
						enums.put(serviceEnumKey.toString(), serviceEnums.get(serviceEnumKey.toString()));
					}
					
				}else if(serviceCodeName.equals(BasicCode) || serviceCodeName.contains(ApplicationEnvironmentConfig.applicationName) ||  serviceCodeName.contains("maozi-cloud-gateway")){
					
					CodeHashMap codeHashMap = new CodeHashMap();
					JSONObject serviceCodes = codeMaps.getJSONObject(serviceCodeName);
					for(Object serviceCodeKey:serviceCodes.keySet()) {
						codeHashMap.put(MapperUtils.json2pojo(serviceCodes.get(serviceCodeKey.toString()).toString(), CodeAttribute.class));
					}
					newCodeDatas.put(serviceCodeName.toString(), codeHashMap);
					
				}
				
			}
			codeDatas = newCodeDatas;
			
		} catch (Exception e) {e.printStackTrace();}

	}
	
	@GetMapping("/")
	@Operation(summary = "详情")
	public AbstractBaseResult<Map<String, String>> application() {
		return success(new HashMap<String,String>() {{
			put("applicationName", ApplicationEnvironmentConfig.applicationName);
			put("details", ApplicationEnvironmentConfig.details);
			put("version", ApplicationEnvironmentConfig.version);
			put("environment", ApplicationEnvironmentConfig.environment);
		}});
	}

	@RestService
	@Tag(name = "【全局】枚举信息")
	public class RestApplicationCode {
		
		@GetMapping("/error/code/list")
		@Operation(summary = "错误编码列表")
		public AbstractBaseResult<Map<String, CodeHashMap>> errorCodeList() {
			return success(codeDatas);
		}
		
		@GetMapping("/enum/list")
		@Operation(summary = "列表")
		public AbstractBaseResult<Map<String, Object>> list() {
			return success(enums);
		}

		@GetMapping("/enum/{name}/get")
		@Operation(summary = "详情")
		public AbstractBaseResult<Object> get(@PathVariable("name") String name) {
			return success(enums.get(name));
		}
		
	}
	
	@GetMapping("/error")
	@Operation(summary = "【服务测试】测试失败结果")
	public ErrorResult error() {
		return error(code(500));
	}
	
	@GetMapping("/base/enum")
	@Operation(summary = "【服务测试】模板枚举")
	public AbstractBaseResult<EnumResult> baseEnum() {
		return success(EnumResult.builder().value(0).desc("测试枚举").build());
	}

}
