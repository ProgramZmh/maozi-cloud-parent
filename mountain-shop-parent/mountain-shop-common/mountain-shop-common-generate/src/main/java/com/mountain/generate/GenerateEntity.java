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

package com.mountain.generate;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import com.mountain.base.AbstractBaseDomain;
import com.mountain.datasource.C3P0;
import com.mountain.entity.EntityData;
import com.mountain.tool.SQLType;
import lombok.Data;


/**
 * 
 * 	功能说明：生成Do
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-20 ：10:22:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


@Data
public class GenerateEntity {

	public static Class parentClass = AbstractBaseDomain.class;

	public static List<EntityData> generate(String dateBaseName, String packageName, String Pash) throws Exception {

		StringBuilder entity =null;
		List<String> tables = new ArrayList<>();
		List<String> parentFields = null;
		List<String> fields = null;
		Map<String,EntityData> entityClasss = new HashMap<>();
		Map<String,List<String>> oneToOne=new HashMap<>();
		Map<String,List<String>> oneToMany=new HashMap<>();
		
		
		/* 初始化数据源 Begin */
		Connection connection = C3P0.getDBManager().createDBManager();
		/* 初始化数据源 Begin */
		
		
		

		/* 获取数据库的所有表名字 Begin */
		ResultSet rSet1 = connection.createStatement().executeQuery(
				"select TABLE_NAME from information_schema.Tables where TABLE_SCHEMA='" + dateBaseName + "'");
		while (rSet1.next()) {
			tables.add(rSet1.getString(1));
		}
		/* 获取数据库的所有表名字 End */

		
		
		
		
		/* 是否有父类 Begin */
		if (parentClass != null) {
			parentFields = new ArrayList<>();
			for (Field field : parentClass.getDeclaredFields()) {
				parentFields.add(field.getName());
			}
		}
		/* 是否有父类 End */
		
		
		

		/* 开始生成entity代码 Begin */
		List<String> list = null;
		for (String tableName : tables) {
			entity= new StringBuilder();
			
			entity.append("package " + packageName + ";\r\n");

			
			
			/* 导包 Begin */
			entityImport(entity, tableName);
			/* 导包 End */
			
			

			entity.append("\r\npublic class " + SQLType.initial(tableName) + "Do");

			
			
			/* 继承父类 Begin */
			if (parentClass != null) {
				entity.append(" extends AbstractBaseDomain implements Serializable");
			}
			entity.append("{\r\n");
			/* 继承父类 Begin */
			
			
			

			/* 数据库查询并写入文件每个表的所有字段 Begin */
			ResultSet rSet2 = connection.createStatement().executeQuery("SHOW FULL FIELDS FROM `" + tableName + "`");
			list = new ArrayList<>();
			fields = new ArrayList<>();
			while (rSet2.next()) {
				
				

				/* 判断父级是否有相同属性 Begin */
				String cname = rSet2.getString(1);
				fields.add(cname);
				if (parentClass != null) {
					if (parentFields.contains(cname)) {
						continue;
					}
				}
				/* 判断父级是否有相同属性 End */
				
				

				String ctype = rSet2.getString(2);
				String cComment = rSet2.getString(9);
				list.add(cname);
				
				
				
				/* 判断是否一对一关系 Begin */
				String [] foreignKey = cname.split("Id");
				if (foreignKey.length==1 && tables.contains(foreignKey[0])) {
					if(!oneToOne.containsKey(tableName)) {
						oneToOne.put(tableName, new ArrayList<String>() {{
							this.add(foreignKey[0]);
						}});
					}else {
						oneToOne.get(tableName).add(foreignKey[0]);
					}
					entity.append("\r\n	private " + SQLType.initial(foreignKey[0]) + "Do " + foreignKey[0] + ";//" + cComment + "\r\n");
				/* 判断是否一对一关系 End */
					
					
					
				/* 判断是否一对多关系 Begin */
				}else if(foreignKey.length==2 && tables.contains(foreignKey[0]) && foreignKey[1].equals("s")) {
					if(!oneToMany.containsKey(tableName)) {
						oneToMany.put(tableName, new ArrayList<String>() {{
							this.add(foreignKey[0]);
						}});
					}else {
						oneToMany.get(tableName).add(foreignKey[0]);
					}
					entity.append("\r\n	private List<" + SQLType.initial(foreignKey[0]) + "Do> " + foreignKey[0] + "s;//" + cComment + "\r\n");
				/* 判断是否一对多关系 Begin */
					
				}else {
					entity.append("\r\n	private " + SQLType.types(ctype) + " " + cname + ";//" + cComment + "\r\n");
				}
				
				
				
				
				
				
				
				
				

			}
			/* 数据库查询并写入文件每个表的所有字段 End */

			entity.setLength(entity.length() - 4);
			entity.append("\r\n\r\n}");

			/* 创建文件 Begin */
			SQLType.fileCreate(Pash + "\\" + (packageName.replace(".", "\\")), tableName + "Do", entity);
			/* 创建文件 End */

			entityClasss.put(tableName,new EntityData(

					packageName + "." + SQLType.initial(tableName) + "Do", 
					fields, 
					tableName, 
					packageName

			));

		}
		
		/* 一对一元素匹配 Begin */
		for(String entityName:oneToOne.keySet()) {
			for(String foreign:oneToOne.get(entityName)) {
				List<EntityData> foreignEntity = entityClasss.get(entityName).getForeignEntityOne();
				if(StringUtils.isEmpty(foreignEntity)) {
					foreignEntity=new ArrayList<>();
				}
				foreignEntity.add(entityClasss.get(foreign));
				entityClasss.get(entityName).setForeignEntityOne(foreignEntity);
			}
			
		}
		/* 一对一匹配 End */
		
		
		/* 一对一多素匹配 Begin */
		for(String entityName:oneToMany.keySet()) {
			for(String foreign:oneToMany.get(entityName)) {
				List<EntityData> foreignEntity = entityClasss.get(entityName).getForeignEntityMany();
				if(StringUtils.isEmpty(foreignEntity)) {
					foreignEntity=new ArrayList<>();
				}
				foreignEntity.add(entityClasss.get(foreign));
				entityClasss.get(entityName).setForeignEntityMany(foreignEntity);
			}
			
		}
		/* 一对一多配 End */
		
		
		/* 开始生成entity代码 End */

		return new ArrayList<EntityData>(entityClasss.values());

	}

	private static void entityImport(StringBuilder entity, String tableName) {
		entity.append("\r\n");
		entity.append("import java.util.List;\r\n");
		entity.append("import java.io.Serializable;\r\n");
		entity.append("import com.baomidou.mybatisplus.annotation.TableName;\r\n");
		entity.append("import com.mountain.base.AbstractBaseDomain;\r\n");
		entity.append("import lombok.AllArgsConstructor;\r\n");
		entity.append("import lombok.Builder;\r\n");
		entity.append("import lombok.Data;\r\n");
		entity.append("import lombok.NoArgsConstructor;\r\n\r\n");
		entity.append("@Data\r\n");
		entity.append("@AllArgsConstructor\r\n");
		entity.append("@NoArgsConstructor\r\n");
		entity.append("@TableName(\"" + tableName + "\")\r\n");
		entity.append("@Builder");
	}

}
