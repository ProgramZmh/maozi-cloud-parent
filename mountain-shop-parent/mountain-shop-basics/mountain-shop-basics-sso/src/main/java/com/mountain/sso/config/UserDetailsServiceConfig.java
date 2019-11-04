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

package com.mountain.sso.config;

import javax.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.mountain.factory.result.AbstractBaseResult;
import com.mountain.factory.result.success.SuccessResult;
import com.mountain.sso.api.UserDetailsServiceRpc;

/**
 * 
 * 	功能说明：UserDetails认证配置
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-12 ：3:09:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Component
public class UserDetailsServiceConfig implements UserDetailsService{

	@Resource
	private UserDetailsServiceRpc userDetailsServiceRpc;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AbstractBaseResult userDetailsResult = userDetailsServiceRpc.loadUserByUsername(username);
		if(userDetailsResult.ifStatus()) {
			return (UserDetails) ((SuccessResult)userDetailsResult).getData();
		}
		return null;
	}
	
	
}
