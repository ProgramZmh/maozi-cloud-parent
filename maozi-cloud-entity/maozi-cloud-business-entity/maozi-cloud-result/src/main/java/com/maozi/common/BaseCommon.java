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

package com.maozi.common;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Tags;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.maozi.base.IEnum;
import com.maozi.base.enums.EnvironmentType;
import com.maozi.base.param.ValidCollectionParam;
import com.maozi.common.result.code.CodeAttribute;
import com.maozi.common.result.code.CodeHashMap;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.common.result.success.SuccessResult;
import com.maozi.dingding.DingDingMessage;
import com.maozi.tool.ApplicationEnvironmentConfig;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.cglib.CglibUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import net.sf.cglib.core.Converter;


@Data
public class BaseCommon implements Serializable {

	public static final String BasicCode = "maozi-cloud-base-code";

	public static Map<String, CodeHashMap> codeDatas = new HashMap<>();

	public static final Logger log = LoggerFactory.getLogger(BaseCommon.class);
	
	public static ThreadLocal<StringBuilder> sql = new ThreadLocal<StringBuilder>();

	public static FastDateFormat DETAULT_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	public static ConcurrentHashMap<String, Map<String, String>> adminHealthError = new ConcurrentHashMap<String, Map<String, String>>();

	
	public BaseCommon() {
		
//		validator = Validation.buildDefaultValidatorFactory().getValidator();
		
	}
	
	protected String getServiceName() { return ""; }
	
	protected String getAbbreviationModelName() { return "组件"; };
	

	public CodeAttribute code(Integer code) {
		return getCode(null, code);
	}

	public CodeAttribute code(String key, Integer code) {
		return getCode(key, code);
	}

	private CodeAttribute getCode(String key, Integer code) {

		CodeAttribute returnResult = null;

		if (isNull(key) && code >= 1 && code < 1000) {
			returnResult = codeDatas.get(BasicCode).get(code);
		}

		if (isNull(key)) {
			key = ApplicationEnvironmentConfig.applicationName+"-service"+"-"+getServiceName();
		}

		if (isNull(returnResult)) {
			returnResult = codeDatas.get(key+"-code").get(code);
		}

		if (isNull(returnResult)) {
			returnResult = codeDatas.get(BasicCode).get(2);
		}

		return returnResult;
	}

	public CodeAttribute code(String key, String valueKey) {
		
		if(isNull(key)) {
			key=ApplicationEnvironmentConfig.applicationName+"-service"+"-"+getServiceName();
		}else {
			key=ApplicationEnvironmentConfig.applicationName+"-service-"+key;
		}

		CodeAttribute returnResult = codeDatas.get(key+"-code").get(valueKey);

		if (isNull(returnResult)) {
			returnResult = codeDatas.get(BasicCode).get(2);
		}

		return returnResult;
	}
	
	public static CodeAttribute baseCode(Integer code) {
		
		CodeAttribute returnResult = codeDatas.get(BasicCode).get(code);

		if (isNull(returnResult)) {
			returnResult = codeDatas.get(BasicCode).get(2);
		}
		
		return returnResult;
	}

	public static Boolean isNull(Object data) {
		return Objects.isNull(data);
	}

	public static Boolean isNotNull(Object data) {
		return Objects.nonNull(data);
	}
	
	public static Boolean isEmpty(String data) {
		return StringUtils.isEmpty(data);
	}
	
	public static Boolean isNotEmpty(String data) {
		return StringUtils.isNotEmpty(data);
	}
	
    public static Boolean collectionIsNotEmpty(Collection<?> data) {
    	return CollectionUtil.isNotEmpty(data);
	}
    
    public static Boolean collectionIsEmpty(Collection<?> data) {
    	return CollectionUtil.isEmpty(data);
	}
    
    public static void checkBoolThrowError(Boolean bool,String message) {
    	if(!bool) {
    		throw new BusinessResultException(400,message,200);
    	}
    }
	
	public static <T> T isNullThrowError(T data,String message) {
		
		if(isNull(data)) { 
			throw new BusinessResultException(404,message+"不存在",200);
		}
		
		return data;
		
	}
	
	public static String isEmptyThrowError(String data,String message) {
		
		if(isEmpty(data)) { 
			throw new BusinessResultException(404, message+"不存在",200);
		}
		
		return data;
		
	}
	
	public static <T> Collection<T> collectionIsEmptyThrowError(Collection<T> datas,String message) {
		
		if(collectionIsEmpty(datas)) { 
			throw new BusinessResultException(404, message+"不存在",200);
		}
		
		return datas;
	}
	
	public static <T extends Collection<?>> Collection listNullAssignment(T datas) {
		return isNull(datas) ? Lists.newArrayList() : datas;
	}
	
	//TODO
	public static void validate(@Valid Object obj){
        
    }
	
	public static <T> T copy(Object data,Class<T> targetClass){
		
		if(isNull(data)) {
			return null;
		}
		
		try {
			
			return CglibUtil.copy(data, targetClass,new Converter() {
				@Override
				public Object convert(Object value, Class target, Object context) {
					
					if(isNotNull(value)) {
					
						if(value.getClass().getName().contains("Double")) {
							return value;
						}else if(value.getClass().getName().contains("Do") || value.getClass().getName().contains("Dto")) {
							return CglibUtil.copy(value,target);
						}else if(target.isArray()){
							
							List valueList = (List)value;
							
							Class componentType = target.getComponentType();
							
							Object [] arrayInstance = (Object[]) Array.newInstance(componentType, valueList.size());
							
							for(int i=0;i<arrayInstance.length;i++) {
								arrayInstance[i]= copy(valueList.get(i), componentType);
							}
							
							return arrayInstance; 
							
						}
						
					}
					
					return value;
					
				}
				
			});
			
		}catch (Exception e) { 
			throw e;
		}
		
	}
	
	public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target){
		
		if(collectionIsEmpty(source)) {
			return Lists.newArrayList();
		}

		try {
			
			return CglibUtil.copyList(source, target,new Converter() {
				
				@Override
				public Object convert(Object value, Class target, Object context) {
					
					if(isNotNull(value)) {
						
						if(value.getClass().getName().contains("Double")) {
							return value;
						}else if(value.getClass().getName().contains("Do") || value.getClass().getName().contains("Dto")) {
							return CglibUtil.copy(value,target);
						}else if(target.isArray()){
							
							List valueList = (List)value;
							
							Class componentType = target.getComponentType();
							
							Object [] arrayInstance = (Object[]) Array.newInstance(componentType, valueList.size());
							
							for(int i=0;i<arrayInstance.length;i++) {
								arrayInstance[i]= copy(valueList.get(i), componentType);
							}
							
							return arrayInstance; 
							
						}
						
					}
					
					return value;
					
				}
			});
			
		}catch (Exception e) { 
			throw e;
		}
		
	}
	
	public static <T extends IEnum> T getEnum(Integer value,Class<T> clazz) {
		
		T[] values = clazz.getEnumConstants();
		
		for(T iEnum : values) {
			
			if(iEnum.getValue() == value) {
				return iEnum;
			}
			
		}
		
		return null;
		
	}
	
	public static <T extends IEnum> T getEnumNullThread(Integer value,Class<T> clazz,String message) {
		
		T[] values = clazz.getEnumConstants();
		
		for(T iEnum : values) {
			
			if(iEnum.getValue() == value) {
				return iEnum;
			}
			
		}
		
		throw new BusinessResultException(404,message+"不存在",200);
		
	}

	private static void initResponse() {
		HttpServletResponse response = getResponse();
		if (isNotNull(response)) {
			response.setHeader("Content-Type", "application/vnd.api+json");
		}
	}

	public static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (isNull(servletRequestAttributes)) {
			return null;
		}
		return servletRequestAttributes.getRequest();
	}

	public static HttpServletResponse getResponse() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (isNull(servletRequestAttributes)) {
			return null;
		}
		return servletRequestAttributes.getResponse();
	}

	public static <T> ErrorResult<T> error(CodeAttribute codeData) {
		initResponse();
		return new ErrorResult<T>(codeData);
	}

	public static <T> ErrorResult<T> error(CodeAttribute codeData, Integer httpCode) {
		initResponse();
		return new ErrorResult<T>(httpCode,codeData);
	}

	public static <T> SuccessResult<T> success(T attributes) {
		initResponse();
		return new SuccessResult<T>(attributes);
	}

	// TODO
	public static String getCurrentUserName() {
		return null;
	}
	
	// TODO
	public static List<String> getPermissions() {
		return null;
	}
	
	//TODO
	public static String getCurrentClientId() {
		return null;
	}

	public static StringBuilder appendLog(Map<String, String> logs) {

		StringBuilder sb = new StringBuilder();

		for (String key : logs.keySet()) {
			sb.append("[ " + key + "：" + logs.get(key) + " ]  ");
		}
		sb.delete(0, 2);
		sb.delete(sb.length() - 4, sb.length());

		return sb;
	}

	public static void errorAlarm(String key, Map<String, String> errorLogs) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[ TID ]"+"："+getTraceId()+"\r\n");
		sb.append("[ TIME ]"+"："+DETAULT_DATE_FORMAT.format(new Date())+"\r\n");
        
        for(String logKey:errorLogs.keySet()) {
        	sb.append("[ "+logKey+" ]"+"："+errorLogs.get(logKey)+"\r\n");
        }
		
        adminHealthError.put(key, errorLogs);
        
		ActiveSpan.tag("error", sb.toString());
		
		DingDingMessage message = new DingDingMessage(sb.toString());
		
    	HttpHeaders headers = new HttpHeaders();
    	
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        
        headers.setContentType(type);
        
        HttpEntity<String> formEntity = new HttpEntity<String>(JSONObject.toJSONString(message).toString(), headers);
        
//        asyncRestTemplate.postForEntity("https://oapi.dingtalk.com/robot/send?access_token="+ApplicationEnvironmentConfig.dingdingToken,formEntity,String.class);
        
	}
	
	public static Boolean isEnvironment(EnvironmentType type) {
		return type.getDesc().equals(ApplicationEnvironmentConfig.environment);
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
	
	public static void throwSystemError(Throwable t) {
		
		StringWriter sw = new StringWriter();
		
		PrintWriter pw = new PrintWriter(sw);

		t.printStackTrace(pw);
		
		error(sw.toString());
		
		throw new BusinessResultException(500,"内部服务错误",200);
		
	}
	
	public static void throwSystemError(Throwable t,String message) {
		
		StringWriter sw = new StringWriter();
		
		PrintWriter pw = new PrintWriter(sw);

		t.printStackTrace(pw);
		
		error(sw.toString());
		
		throw new BusinessResultException(500,"内部服务错误",200);
		
	}
	
	public static void throwSystemError(String message) {
		
		error(message);
		
		throw new BusinessResultException(500,"内部服务错误",200);
		
	}

	public static void debug(String message) {
		log.debug(message);
		ActiveSpan.debug(message);
	}

	public static void info(String message) {
		log.info(message);
		ActiveSpan.info(message);
	}

	public static void error(String message) {
		log.error(message);
		ActiveSpan.error(message);
	}

	public static String getTraceId() {
		
		String traceId = TraceContext.traceId();
		
		if (isNull(traceId)) {
			return null;
		}
		
		if (traceId.equals("Ignored_Trace")) {
			return null;
		}
		
		return traceId;
		
	}

	@Trace(operationName = "入参值")
	@Tags({ @Tag(key = "入参值", value = "arg[0]") })
	public void functionParam(Object param) {}

	@Trace(operationName = "返回值")
	@Tags({ @Tag(key = "返回值", value = "arg[0]") })
	public void functionReturn(Object result) {}

	@Trace(operationName = "错误值")
	@Tags({ @Tag(key = "错误值", value = "arg[0]") })
	public void functionError(Object errorMessage) {}

	public static void clear() {
		
		sql.remove();
		
		MDC.clear();
		
	}

}