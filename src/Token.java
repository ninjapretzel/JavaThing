
import java.util.HashMap;
import java.util.Map;

import static Constants.JAVA.*;

/** Stupid class holding info for a token */
public class Token {
	
	/** Invalid token for general WTF moments */
	public static final Token INVALID = new Token(INVALID_TYPE);
	/** Done Token **/
	public static final Token DONE_TOKEN = new Token("DONE!", INVALID_TYPE);
	
	/** hold a ton of final/fixed tokens we can expect a ton of */
	public static Map<String, Token> fixedTokens = new HashMap<>();
	/** Function to get/create fixed tokens to save on memory */
	public static Token fixed(String type) {
		if (fixedTokens.containsKey(type)) { return fixedTokens.get(type); }
		Token t = new Token(type);
		fixedTokens.put(type, t);
		return t;
	}
	
	/** Actual content of what was read from the file */
	public String content;
	
	/** Type of token. Same as content for fixed tokens (whitespace, keywords, punctuation) */
	public String type;
	
	/** Constructor that sets type/content to the same value */
	public Token(String content) {
		this.content = type = content;
	}
	
	/** Constructor that provides a content and type */
	public Token(String content, String type) {
		this.type = type;
		this.content = content;
	}
	
	/** Returns true if this token is a specific 'kind' */
	public boolean is(String kind) { return type.equals(kind); }
	
	/** Returns true if this token is one of a set of given kinds */
	public boolean is(String... kinds) {
		for (int i = 0; i < kinds.length; i++) {
			if (type.equals(kinds[i])) { return true; }
		}
		return false;
	}
	
	/** Returns true so long as the type is not INVALID */
	public boolean isValid() { return !type.equals(INVALID_TYPE); }
	
	/** Returns true for whitespace tokens */
	public boolean isWhitespace() { 
		return content.equals(" ") || content.equals("\t") || content.equals("\n");
	}
		
	/** Human Readable to-string */
	public String toString() {
		if (content.equals(" ")) { return "SPACE"; }
		if (content.equals("\t")) { return "TAB"; }
		if (content.equals("\n")) { return "NEWLINE"; }
		// Comparison by reference is faster than .equals
		// and this should only run in the case 
		// where the 1-param constructor was used, and they have the same ref.
		if (type != content) { return type + ": [" + content + "]"; }
		return type;
	}
	
	
}
