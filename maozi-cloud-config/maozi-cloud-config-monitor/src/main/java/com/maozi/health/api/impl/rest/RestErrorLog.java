package com.maozi.health.api.impl.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "【全局】告警日志")
@RequestMapping("/application/erroLog")
public class RestErrorLog {

	@PostMapping("/removeAll")
	@Operation(summary = "清空")
	public AbstractBaseResult<Void> removeAll() {
		BaseCommon.adminHealthError.clear();
		return BaseCommon.success(null);
	}

	@PostMapping("/{id}/remove")
	@Operation(summary = "删除")
	public AbstractBaseResult<Void> remove(@PathVariable String id) {
		BaseCommon.adminHealthError.remove(id);
		return BaseCommon.success(null);
	}

}
