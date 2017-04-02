package com.epsilon.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Apache Ah64
 */
public class Formatter {
	
	/**
	 * The consonants in the English language.
	 */
	private static final String[] CONSONANTS = { "a", "e", "i", "o", "u" };
	
	/**
	 * An enumeration holding the format types.
	 * @author Apache Ah64
	 */
	public enum FormatTypes {
		
		/**
		 * The display name format type.
		 */
		DISPLAY_NAME {
			@Override
			public String format(String string) {
				string = string.replace("_", " ");
				final StringBuilder builder = new StringBuilder();
				boolean uppered = false;
				for(int index = 0; index < string.length(); index++) {
					final char c = string.charAt(index);
					final boolean canUpper = !uppered && validCharacter(c);
					builder.append(canUpper? Character.toUpperCase(c) : c);
					if (canUpper)
						uppered = true;
				}
				return builder.toString();
			}
		},
		
		/**
		 * The protocol format type.
		 */
		PROTOCOL {
			@Override
			public String format(String string) {
				return string.toLowerCase().replace(" ", "_");
			}
		},
		
		/**
		 * The chat message format type.
		 */
		CHAT_MESSAGE {
			@Override
			public String format(String string) {
				string = string.replace("_", " ");
				final StringBuilder builder = new StringBuilder();
				boolean containsValidCharacter = false;
				int needUppered = 0;
				for(int index = 0; index < string.length(); index++) {
					final char c = string.charAt(index);
					if(!containsValidCharacter) {
						if(((c == '.' || c == ' ') || !containsValidCharacter) && needUppered == 0 && !(containsValidCharacter && Character.isLowerCase(c))) {
							needUppered = 2;
						}
					}
					builder.append(validCharacter(c) && needUppered == 2 ? Character.toUpperCase(c) : Character.toLowerCase(c));
					if(validCharacter(c) && needUppered > 0) {
						if(needUppered == 1) {
							containsValidCharacter = true;
						}
						needUppered--;
					}
				}
				String input = builder.toString();
			    final Pattern regex = Pattern.compile("([\\?!\\.]\\s*)([a-z])");
			    Matcher matcher = regex.matcher(input);
			    while(matcher.find()) {
			        input = matcher.replaceFirst(matcher.group(1) + matcher.group(2).toUpperCase());
			        matcher = regex.matcher(input);
			    }
				return input.length() <= 0 ? "" : String.format("%s%s", Character.toUpperCase(input.charAt(0)), input.substring(1));
			}
		};
		
		/**
		 * Format a string.
		 * @param string The string to format.
		 * @return The formatted string.
		 */
		public abstract String format(String string);
		
	}
	
	/**
	 * Format's the specific string on the given type.
	 * @param type The given type.
	 * @param string The specific string.
	 * @return The formatted string.
	 */
	public static final String format(FormatTypes type, String string) {
		return (type == null || string == null)  ? null : type.format(string);
	}
	
	/**
	 * Check if a character is valid.
	 * @param character The character to check.
	 * @return {@code true} If the character is valid.
	 */
	public static final boolean validCharacter(char character) {
		return ("" + character).matches("^[a-zA-Z0-9_]+");
	}
	
	/**
	 * Check if two strings equal after formatting.
	 * @param type The type.
	 * @param first The first string. 
	 * @param second The second string.
	 * @return {@code true} If both aren't null and equals.
	 */
	public static final boolean equals(final FormatTypes type, final String first, final String second) {
		final String firstFormatted = format(type, first), secondFormatted = format(type, second);
		return firstFormatted != null && secondFormatted != null && firstFormatted.equals(secondFormatted);
	}
	
	/**
	 * Check is the indefinite article should be applied to the word.
	 * @param word The word.
	 * @return If it should be applied {@code true}.
	 */
	public static boolean isIndefinite(String word) {
		word = word.toLowerCase();
		for(final String consonant : CONSONANTS) {
			if(word.startsWith(consonant)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Capitalize a character in a word.
	 * @param word The word.
	 * @param index The character's index.
	 * @return The word with the capitalized character.
	 */
	public static String capitalize(String word, int index) {
		final StringBuilder builder = new StringBuilder();
		if(index > 0) {
			builder.append(word.substring(0, index - 1));
		}
		builder.append(word.charAt(index)+"".toUpperCase());
		builder.append(word.substring(index + 1, word.length()));
		return builder.toString();
	}
	
}