package com.secutix.plugin.util;

import org.junit.Assert;
import org.junit.Test;

public class TestTranslationHelper {

	@Test
	public void testDefaultTranslation() {
		String res = TranslationHelper.translateMessage("en",
				"[en]Barcode cancelled for ticket number :");
		Assert.assertEquals("Barcode cancelled for ticket number :", res);

		res = TranslationHelper.translateMessage("en", "Barcode cancelled for ticket number :");
		Assert.assertEquals("Barcode cancelled for ticket number :", res);
		res = TranslationHelper.translateMessage("fr", "Barcode cancelled for ticket number :",
				"[fr]Code-barres annulé pour le billet");
		Assert.assertEquals("Code-barres annulé pour le billet", res);
	}

}
