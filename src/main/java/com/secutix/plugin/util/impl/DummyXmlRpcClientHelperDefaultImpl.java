package com.secutix.plugin.util.impl;

import com.secutix.plugin.util.XmlRpcClientHelper;

public class DummyXmlRpcClientHelperDefaultImpl implements XmlRpcClientHelper {

	@Override
	public Object execute(String serverUrl, String username, String password, String method, Object[] params) {
		return null;
	}
}
