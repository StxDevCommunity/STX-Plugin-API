package com.secutix.plugin.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.secutix.plugin.v1_0.AbstractStxPlugin;

public class PluginHelper {

	public static Map<String, Method> extractBatchesMethods(Object plugin) {
		if (plugin == null) {
			throw new IllegalArgumentException("Plugin cannot be null");
		}

		Map<String, Method> res = new HashMap<>();
		Method[] methods = plugin.getClass().getMethods();
		for (Method m : methods) {
			@SuppressWarnings("unchecked")
			StxFunction function = getSecutixFunctionFromMethod(
					(Class<? extends AbstractStxPlugin>) plugin.getClass(), m);
			if (function != null && ValidationHelper.isSecutixFunctionAnnotationValid(function)
					&& function.batchFunction()) {
				res.put(function.functionCode(), m);
			}
		}
		return res;
	}

	public static StxFunction getSecutixFunctionFromMethod(
			Class<? extends AbstractStxPlugin> pluginClass, Method m) {
		StxFunction stxFunction = m.getAnnotation(StxFunction.class);
		if (stxFunction == null) {
			// Let us try to find it in superclass.
			try {
				Method superClassMethod = pluginClass.getSuperclass().getMethod(m.getName(),
						m.getParameterTypes());

				stxFunction = superClassMethod.getAnnotation(StxFunction.class);
			} catch (Exception e) {
				// No method found. Normal.
				stxFunction = null;
			}
		}
		return stxFunction;
	}
}
