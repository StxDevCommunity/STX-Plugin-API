package com.secutix.plugin.util;

import java.util.List;

public interface UniqueValuesHelper {

	/**
	 * Returns next unique value, associated to the given discriminator. (means that
	 * discriminator-uniqueValue is always unique).
	 * 
	 * @param discriminator on empty string. Max length 10 chars.
	 * @return unique long.
	 */
	Long getNextUniqueValue(String discriminator);

	/**
	 * Returns count next unique values, associated to the given discriminator.
	 * 
	 * @param discriminator non empty string. Max length 10 chars.
	 *  @param count number of values to generate.
	 *  @return a list of unique longs
	 */

	List<Long> getNextUniqueValues(String discriminator, int count);

}
