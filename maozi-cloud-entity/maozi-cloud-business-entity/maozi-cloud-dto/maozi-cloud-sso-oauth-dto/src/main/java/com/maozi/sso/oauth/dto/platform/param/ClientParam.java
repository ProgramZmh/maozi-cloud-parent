package com.maozi.sso.oauth.dto.platform.param;

import javax.validation.constraints.NotNull;

import com.maozi.base.AbstractBaseDtomain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ClientParam extends AbstractBaseDtomain {

	@Schema(description = "客户端ID")
	@NotNull(message = "客户端ID不能为空")
	private String clientId;
	
	@Schema(description = "客户端密钥")
	@NotNull(message = "客户端密钥不能为空")
	private String clientSecret;
	
}
