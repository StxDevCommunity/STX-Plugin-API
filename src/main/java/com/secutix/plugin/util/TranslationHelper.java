package com.secutix.plugin.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationHelper {
	public static String translateMessage(String currentLanguage, String message, String... translations) {
		Map<String, String> translationMap = new HashMap<>();

		if (translations != null) {
			for (String trStr : translations) {
				Translation tr = extractTranslation(trStr);
				translationMap.put(tr.languageCode(), tr.message());
			}
		}

		Translation tr = extractTranslation(message);

		translationMap.put(tr.languageCode(), tr.message());

		String translatedMessage = tr.message();

		if (translationMap.containsKey(currentLanguage)) {
			translatedMessage = translationMap.get(currentLanguage);
		}
		return translatedMessage;
	}

	private static Pattern pattern = Pattern.compile("\\[(\\w+)\\]\\s*(.*)");

	public static Translation extractTranslation(String message) {
		Matcher m = pattern.matcher(message);
		if (m.matches()) {
			return Translation.of(m.group(1), m.group(2));
		}
		return Translation.of("None", message);
	}

	public static class Translation {
		private final String languageCode;
		private final String message;

		public String languageCode() {
			return languageCode;
		}

		public String message() {
			return message;
		}

		private Translation(String first, String second) {
			super();
			this.languageCode = first;
			this.message = second;
		}

		public static Translation of(String first, String second) {
			return new Translation(first, second);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
			result = prime * result + ((message == null) ? 0 : message.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Translation other = (Translation) obj;
			if (languageCode == null) {
				if (other.languageCode != null) {
					return false;
				}
			} else if (!languageCode.equals(other.languageCode)) {
				return false;
			}
			if (message == null) {
				if (other.message != null) {
					return false;
				}
			} else if (!message.equals(other.message)) {
				return false;
			}
			return true;
		}

	}

}
