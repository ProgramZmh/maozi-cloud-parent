
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

package com.maozi.generate.code.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import com.maozi.generate.code.entity.DataSourceConfig;
import com.mchange.v2.c3p0.ComboPooledDataSource;


/**
 * 
 * 	功能说明：C3P0 DataSource
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-20 : 12:09:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


public class C3P0 {
	ComboPooledDataSource c = null;
	private C3P0() {
		try { 
			c = new ComboPooledDataSource();
			c.setDriverClass("com.mysql.cj.jdbc.Driver");//FinancingProduct
			c.setJdbcUrl("jdbc:mysql://"+DataSourceConfig.JDBCURL+"?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&nullCatalogMeansCurrent=true");
			c.setUser(DataSourceConfig.USER);
			c.setPassword(DataSourceConfig.PASSWORD); 
			c.setMaxPoolSize(1);  
		} catch (Exception e) {

		}
	}
	private static C3P0 c3p0=new C3P0();
	
	public static C3P0 getDBManager(){
		return c3p0;
	}
	public Connection createDBManager() throws Exception{
		return c.getConnection();
	}
	public static void releaseConnection(Connection connection){
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
