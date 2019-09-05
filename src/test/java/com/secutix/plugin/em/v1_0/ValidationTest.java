package com.secutix.plugin.em.v1_0;

import com.secutix.plugin.util.SalesContextAware;
import com.secutix.plugin.util.StxFunction;
import com.secutix.plugin.util.ValidationException;
import com.secutix.plugin.v1_0.AbstractStxPlugin;
import com.secutix.plugin.v1_0.dto.ParameterDefinition;
import com.secutix.plugin.v1_0.dto.PluginCallResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ValidationTest {

	AbstractStxPlugin plugin;

	@Test
	public void validPlugin() {
		plugin = new AbstractStxPlugin() {
			@Override
			public String getInterfaceType() {
				return "VALID";
			}

			@Override
			public Map<String, String> getInterfaceNamesByLanguageCode() {
				return Collections.singletonMap("en", "Plugin name");
			}

			@StxFunction(batchFunction = true, functionCode = "ExtPayIn", functionName = "en=Process external payments,fr=Intégrer paiements externes,de=Externe Zahlungen verarbeiten", functionInternalCode = "EXTPAY_INT")
			public PluginCallResult processCall() {
				return null;
			}
		};

		try {
			plugin.validate();
		} catch (ValidationException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void inValidPlugin() {
		plugin = new AbstractStxPlugin() {
			@Override
			public String getInterfaceType() {
				return "INVALIDXXXXXXXXX";
			}

			@Override
			public Map<String, String> getInterfaceNamesByLanguageCode() {
				return Collections.singletonMap("en", "Plugin name");
			}

			@StxFunction(batchFunction = true, functionCode = "ExtPayIn", functionName = "en=Process external payments,fr=Intégrer paiements externes,de=Externe Zahlungen verarbeiten", functionInternalCode = "EXTPAY_INT")
			public PluginCallResult processCall() {
				return null;
			}
		};

		try {
			plugin.validate();
		} catch (ValidationException e) {
			Assert.assertEquals("getInterfaceType=INVALIDXXXXXXXXX must  have a length of less than 15 characters", e.getMessage());
		}

		plugin = new AbstractStxPlugin() {
			@Override
			public String getInterfaceType() {
				return "I";
			}

			@Override
			public Map<String, String> getInterfaceNamesByLanguageCode() {
				return Collections.singletonMap("en", "Plugin name");
			}

			@StxFunction(batchFunction = true, functionCode = "ExtPayIn", functionName = "en=Process external payments,fr=Intégrer paiements externes,de=Externe Zahlungen verarbeiten", functionInternalCode = "EXTPAY_INT")
			public PluginCallResult processCall() {
				return null;
			}
		};

		try {
			plugin.validate();
		} catch (ValidationException e) {
			Assert.assertEquals("getInterfaceType=I must have a length of more than 3 characters", e.getMessage());
		}

		plugin = new AbstractStxPlugin() {
			@Override
			public String getInterfaceType() {
				return "VALID";
			}

			@Override
			public Map<String, String> getInterfaceNamesByLanguageCode() {
				return Collections.singletonMap("en", "Plugin name");
			}

			//@StxFunction(batchFunction = true, functionCode = "ExtPayIn", functionName = "en=Process external payments,fr=Intégrer paiements externes,de=Externe Zahlungen verarbeiten", functionInternalCode = "EXTPAY_INT")
			public PluginCallResult processCall() {
				return null;
			}
		};

		try {
			plugin.validate();
			Assert.fail("Should not be valid");
		} catch (ValidationException e) {
			Assert.assertEquals("At least one function must be declared !", e.getMessage());
		}
	}

	private Optional<String> validationError(AbstractStxPlugin pluginParam) {
		try {
			pluginParam.validate();
			return Optional.empty();
		} catch (ValidationException e) {
			return Optional.of(e.getMessage());
		}

	}

	@Test
	public void salesContextAware() {

		Assert.assertEquals("salesContextAware.salesChannelParamCode= must have a length of more than 3 characters",validationError(new SalesContextAwarePlugin1()).get());
		Assert.assertEquals("salesContextAware.pointOfSalesParamCode= must have a length of more than 3 characters",validationError(new SalesContextAwarePlugin2()).get());
		Assert.assertEquals("Unable to find a parameter defined having the following code :Param1",validationError(new SalesContextAwarePlugin3()).get());
		Assert.assertEquals("Unable to find a parameter defined having the following code :Param2",validationError(new SalesContextAwarePlugin4()).get());
		Assert.assertEquals("",validationError(new SalesContextAwarePlugin5()).orElse(""));



	}


	private static abstract class BasicPlugin extends AbstractStxPlugin {
		@Override
		public String getInterfaceType() {
			return "VALID";
		}

		@Override
		public Map<String, String> getInterfaceNamesByLanguageCode() {
			return Collections.singletonMap("en", "Plugin name");
		}

		@StxFunction(batchFunction = true, functionCode = "ExtPayIn", functionName = "en=Process external payments,fr=Intégrer paiements externes,de=Externe Zahlungen verarbeiten", functionInternalCode = "EXTPAY_INT")
		public PluginCallResult processCall() {
			return null;
		}
	}

	@SalesContextAware(salesChannelParameterCode = "", pointOfSalesParameterCode = "")
	private static class SalesContextAwarePlugin1 extends BasicPlugin {
	}

	@SalesContextAware(salesChannelParameterCode = "Param1", pointOfSalesParameterCode = "")
	private static class SalesContextAwarePlugin2 extends BasicPlugin {
	}

	@SalesContextAware(salesChannelParameterCode = "Param1", pointOfSalesParameterCode = "Param2")
	private static class SalesContextAwarePlugin3 extends BasicPlugin {
	}

	@SalesContextAware(salesChannelParameterCode = "Param1", pointOfSalesParameterCode = "Param2")
	private static class SalesContextAwarePlugin4 extends BasicPlugin {
		@Override
		public List<ParameterDefinition> listParameterDefinitions() {
			List<ParameterDefinition> definitions = new ArrayList<>();
			definitions.add(ParameterDefinition.buildParameterDefinition("Param1", ParameterDefinition.ParameterType.STRING, "en=Sales channel code", true, ""));
			return definitions;
		}
	}

	@SalesContextAware(salesChannelParameterCode = "Param1", pointOfSalesParameterCode = "Param2")
	private static class SalesContextAwarePlugin5 extends BasicPlugin {
		@Override
		public List<ParameterDefinition> listParameterDefinitions() {
			List<ParameterDefinition> definitions = new ArrayList<>();
			definitions.add(ParameterDefinition.buildParameterDefinition("Param1", ParameterDefinition.ParameterType.STRING, "en=Sales channel code", true, ""));
			definitions.add(ParameterDefinition.buildParameterDefinition("Param2", ParameterDefinition.ParameterType.STRING, "en=Pos code", true, ""));
			return definitions;
		}

	}


}
