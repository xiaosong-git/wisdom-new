package com.xdream.wisdom.config;

import com.xdream.kernel.ServerSupport;
import com.xdream.wisdom.util.ParamDef;

public class PaymentInitServer extends ServerSupport {

	@Override
	public void doStart() throws Exception {
		//金蝶配置
		JinDieConfig.APPID = ParamDef.findJDByName("appid");
		JinDieConfig.CLIENT_SECRET = ParamDef.findJDByName("client_secret");
		JinDieConfig.PUBLICKEY = ParamDef.findJDByName("publickey");
		JinDieConfig.DK=ParamDef.findJDByName("dk");
	}

	@Override
	public void doStop() throws Exception {

	}
}
