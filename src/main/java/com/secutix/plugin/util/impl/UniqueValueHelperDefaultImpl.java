package com.secutix.plugin.util.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.secutix.plugin.util.UniqueValuesHelper;

public class UniqueValueHelperDefaultImpl implements UniqueValuesHelper {
	
	private Map<String, Long> uniqueValuesStorage = new HashMap<>();


	@Override
	public Long getNextUniqueValue(String discriminator) {
		if (!uniqueValuesStorage.containsKey(discriminator)) {
			Random r = new Random(discriminator.hashCode());
			uniqueValuesStorage.put(discriminator, Long.valueOf(r.nextInt(100000)));
		}
		long currentValue = uniqueValuesStorage.get(discriminator);
		long incrementedValue = currentValue + 1;
		uniqueValuesStorage.put(discriminator, incrementedValue);
		return incrementedValue;
	}

	@Override
	public List<Long> getNextUniqueValues(String discriminator, int count) {
		List<Long> values = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			values.add(getNextUniqueValue(discriminator));
		}
		return values;
	}

}
