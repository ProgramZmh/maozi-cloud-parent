package com.maozi.config.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.maozi.common.BaseCommon;
import com.maozi.tool.MapperUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class CustomUrlBlockHandler extends BaseCommon implements BlockExceptionHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
		
		MapperUtils.getInstance().writeValue(response.getOutputStream(), error(code(429)).autoIdentifyHttpCode());    
	
	}
   
}