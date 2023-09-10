package com.maozi.sso.oauth.api.rest.v1;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.sso.oauth.api.rest.v1.fallback.OauthTokenServiceRestFallBackFactory;
import com.maozi.sso.oauth.dto.platform.dto.OauthToken;
import com.maozi.sso.oauth.dto.platform.param.ClientParam;
import com.maozi.sso.oauth.dto.platform.param.TokenInfoParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "【三方】授权令牌")
@FeignClient(value = "maozi-cloud-sso",fallbackFactory = OauthTokenServiceRestFallBackFactory.class)
public interface RestOauthTokenServiceV1 {

	@PostMapping("/oauth/token/get")
	@Operation(summary = "获取令牌")
	AbstractBaseResult<OauthToken> restGet(@RequestBody @Valid TokenInfoParam param) throws Exception;
	
	@PostMapping("/oauth/token/{token}/refresh")
	@Operation(summary = "刷新令牌")
	AbstractBaseResult<OauthToken> restRefresh(@PathVariable("token") String refreshToken,@RequestBody @Valid ClientParam param) throws Exception;

	@GetMapping("/oauth/token/{token}/check")
	@Operation(summary = "检查令牌")
	AbstractBaseResult<Map> restCheck(@PathVariable("token") String token);
	
	@GetMapping("/oauth/token/{token}/destroy")
	@Operation(summary = "删除令牌")
	AbstractBaseResult<Void> restDestroy(@PathVariable("token") String token);
	
}
