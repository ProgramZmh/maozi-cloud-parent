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

package com.mountain.mvc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import com.mountain.tool.MapperUtils;


/**
 * 
 * 	功能说明：MVC Converter json转换Pojo
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-31 ：20:16:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


public class StringToOneConverter<T> implements Converter<String, T> {

	@Override
	public T convert(String json) {
		Type type  =  (Class < T > ) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Class clazz = type.getClass();
        T pojo = null;
		try {
			pojo = (T) MapperUtils.json2pojo(json,clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return  pojo;
	}
	

}
