
import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import static Constants.JAVA.*;
import java.util.ArrayList;
import java.util.List;

/** Java Language tokenizer */
public class Tokenizer {
    
    /** Run location for testing Tokenizer */
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        String src = "";
        try {
            StringBuilder b = new StringBuilder();
            File f = new File("Simple.java");
            Scanner sc = new Scanner(f);
            while (sc.hasNext()) {
                b.append(sc.nextLine());
                if (sc.hasNext()) { b.append("\n"); }
            }
            src = b.toString();
        } catch (Exception e) {
            src = "FAILED TO LOAD FILE";
        }
        
        System.out.println("Source Code Loaded:\n");
        System.out.println(src);
        System.out.println("\ntokenizing...");
        
        
        Tokenizer tok = new Tokenizer(src);
        System.out.println(tok.peekToken);
        while (tok.move()) {
			System.out.println(tok.peekToken);
        }
        System.out.println("Tokenizing done. Last token: " + tok.peekToken);
        System.out.println("Line: " + tok.line + " Col: " + tok.col);

    }
		
    /** List of style problem messages */
    public List<String> styleProblems;
	
    
    /** Original source string */
    public String src;
    /** Remaining substring */
    public String remaining;
    
    /** Tracks line number */
    public int line;
    /** Tracks line column */
    public int col;
	
    /** Tracks number of spaces passed */
	public int spaces;
    /** Tracks number of tabs passed */
	public int tabs;
    /** Tracks line column */
	public int newlines;
    
    /** Token ahead of cursor, that hasn't been consumed */
    public Token peekToken;
    /** Last consumed token */
    public Token lastToken;
    /** Last non-whitespace token */
    public Token lastRealToken;
    
	/** Standard constructor, does some pre-processing to the input source code */
    public Tokenizer(String source) {
        // Replace \r\n and \r newlines with just \n, and trim whitespace off the ends
        src = source.replace("\r\n", "\n").replace("\r", "\n");//.trim();
        styleProblems = new ArrayList<>();
		reset();
    }
	
	/** Throws an exception with a given message */
	public void error(String message) {
		throw new RuntimeException(message + "\nToken: " + peekToken + "\nLine " + line + " col " + col);		
	}
	
	/** Checks for a number of spaces, and records a warning if there are not that number of spaces*/
	public void requireSpaces(int n, String message) {
		if (spaces != n) {
			styleProblems.add("Space violation! " +
				message + "-violated at line " + line + " col " + col 
					+ "\n\tExpected " + n + " spaces,"
					+ "\n\tThere were " + spaces + " spaces!"
			);
		}
	}
	
	/** Checks for a number of tabs, and records a warning if there are not that number of tabs*/
	public void requireTabs(int n, String message) {
		if (tabs != n) {
			styleProblems.add("Tab violation! " +
				message + "-violated at line " + line + " col " + col
					+ "\n\tExpected " + n + " tabs,"
					+ "\n\tThere were " + tabs + " tabs!"
			);
		}
	}
	
	/** Checks for a number of newlines, and records a warning if there are not that number of newlines */
	public void requireNewlines(int n, String message) {
		if (newlines != n) {
			styleProblems.add("Newline violation! " +
				message + "-violated at line " + line + " col " + col
					+ "\n\tExpected " + n + " newlines,"
					+ "\n\tThere were " + newlines + " newlines!"
			);
		}
	}
	
	/** Throws an exception if the peekToken is NOT of one of the given types */
	public void require(String... types) {
		if (!peekToken.is(types)) {
			String str = "";
			for (String s : types) { str += s + ", "; }
			error("Expected " + str);
		}
	}
	
	
	/** Throws an exception if the next token is not one of types
	but if it is, consumes it */
	public void requireNext(String... types) {
		require(types);
		next();
	}
	
	
	/** Is the tokenizer out of tokens? */
	public boolean done() {
		return !peekToken.isValid();
	}
    
    /** Resets this Tokenizer to its initial state */
    public void reset() {
        remaining = src;
        line = 1;
        col = 0;
		spaces = newlines = tabs = 0;
        lastRealToken = lastToken = null;
        peekToken = peek();
		
    }
    
	/** Moves forward past all whitespace tokens, until the next non-whitespace token
    Returns the lastRealToken that went out of scope, if you care about it */
    public Token next() {
        if (peekToken.isValid()) {
            Token save = lastRealToken;
            
            if (move()) {
				spaces = tabs = newlines = 0;
                while (peekToken.isValid() && peekToken.isWhitespace()) { move(); }
            }

            return save;
        }
        return peekToken;   
    }
    
	/** Moves forward past the current peek token and all following whitespace
	and gives back the next peek token*/
	public Token nextPeek() {
		next();
		return peekToken;
	}
	
    /** Tries to move past the peek token, and returns true if it did */
    public boolean move() {
        if (peekToken.isValid()) {
            // Forward column by length of consumed content
            if (peekToken.is(NEWLINE)) { 
				col = 0; 
				line++;
				newlines++;
			} else { 
				if (peekToken.is(SPACE)) { spaces++; }
				if (peekToken.is(TAB)) { tabs++; }
				
				col += peekToken.content.length(); 
			}
            
			
			//System.out.println("Moving past [" + peekToken.content + "] len " + peekToken.content.length());
            remaining = remaining.substring(peekToken.content.length());
            lastToken = peekToken;
            if (!peekToken.isWhitespace()) { lastRealToken = peekToken; }
            peekToken = peek();
        }
        return peekToken.isValid();
    }
    
    /** Gets the next token from the source */
    public Token peek() {
        if (remaining.length() == 0) { return Token.DONE_TOKEN; }
        
        // Whitespace
        if (remaining.charAt(0) == ' ') { return Token.fixed(SPACE); }
        if (remaining.charAt(0) == '\t') { return Token.fixed(TAB); }
        if (remaining.charAt(0) == '\n') { return Token.fixed(NEWLINE); }
        if (remaining.charAt(0) == '\"') { return extractString(); }
        if (remaining.charAt(0) == '\'') { return extractChar(); }
        
        for (String p : PUNCTUATION) { if (remaining.startsWith(p)) { return Token.fixed(p); } }
        for (String k : KEYWORDS) { if (remaining.startsWith(k)) { return Token.fixed(k); } }
        
        Matcher nameCheck = NAME_REGEX.matcher(remaining);
        if (nameCheck.lookingAt()) { return new Token(nameCheck.group(), NAME_TYPE); }
        
        Matcher numCheck = NUM_REGEX.matcher(remaining); 
        if (numCheck.lookingAt()) { return new Token(numCheck.group(), NUMBER_TYPE); }
        
        return Token.INVALID;
    }
    
	/** Fixed token returned when a newline is in a string literal */
    private static final Token BAD_STRING_NEWLINE_INSIDE = new Token("Newline in string literal", INVALID_TYPE);
	/** Fixed token return when there is no matching character to close a string literal */
    private static final Token BAD_STRING_NO_MATCHING_QUOTE = new Token("No matching quote for string literal", INVALID_TYPE);
    
	/** Extracts a string region from the code, handling escaped "s */
    private Token extractString() {
        // Find next actual newline
        int nextNL = remaining.indexOf('\n');
        // Make it always greater than any character position...
        if (nextNL == -1) { nextNL = Integer.MAX_VALUE; }
        
        // Look past the first character, since we know that char is already a \"
        int i = remaining.indexOf('\"', 1);
        
        while (true) {
            if (i == -1) { return BAD_STRING_NO_MATCHING_QUOTE; }
            if (i > nextNL) { return BAD_STRING_NEWLINE_INSIDE; }
            if (remaining.charAt(i-1) != '\\') { break; }
            i = remaining.indexOf('\"', i+1);
        }
        
        return new Token(remaining.substring(0, i+1), STRING_TYPE);
    }
    
	/** Extracts a character region from the code, allowing for escaped characters */
    private Token extractChar() {
        char next = remaining.charAt(1);
        String charLit = (next == '\\') ? remaining.substring(0, 4) : remaining.substring(0, 3);
        if (charLit.charAt(charLit.length()-1) != '\'') { 
            return new Token("Incorrect format for char literal!", INVALID_TYPE);
        }
        
        return new Token(charLit, CHAR_TYPE);
    }
}






















