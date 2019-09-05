package com.secutix.plugin.util;

public interface XmlRpcClientHelper {
	public Object execute(String serverUrl, String username, String password, String method, Object[] params);
}
