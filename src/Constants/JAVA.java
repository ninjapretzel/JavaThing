package Constants;

import java.util.regex.Pattern;

public class JAVA {
	
    /// Strings holding an invalid token, that could never be parsed from the file,
    /// To represent specific types of tokens...
	/** Impossible token representing tokens of type Name/Identifier/Symbol */
    public static final String NAME_TYPE = "!NAME";
	/** Impossible token representing tokens of type Number */
    public static final String NUMBER_TYPE = "!NUM";
	/** Impossible token representing tokens of type String */
    public static final String STRING_TYPE = "!STR";
	/** Impossible token representing tokens of type Character */
    public static final String CHAR_TYPE  = "!CHAR";
	
	/** String holding a token that could never be parsed from a file */
	public static final String INVALID_TYPE = "!INVALID";
	
	/** Just a space character */
    public static final String SPACE  = " ";
	/** Just a newline character */
    public static final String NEWLINE  = "\n";
	/** Just a tab character */
    public static final String TAB  = "\t";
	
    /** Fixed keywords in Java */
    public static final String[] KEYWORDS = {
        // Book-keeping
        "import","package",
        // Bindings
        "public","private","protected",
        "static","abstract","final",
        // Types of types
        "class","interface","enum",
        // Declarations
        "extends","implements","throws",
        // Primitives- we actually treat these as identifiers...
        //"int","short","long","byte",
        //"float","double",
        //"boolean",
        //"char",
        //"void",
        // Flow control
        "if", "else", 
        "while","for","do",
        "switch","case",
        "break","continue","default",
        "return",
        // Exception handling
        "throw","catch","try","finally",
        // Other...
        "true","false",
        "null", "new", "this", "super",
        "synchronized","volatile", 
        "assert",
        "instanceof",
        "native", 
        "staticfp",
        "transient",
        
        // Unused, still in spec
        "const", "goto",
    };
    
    /** java punctuation */
    public static final String[] PUNCTUATION = {
        "+", "-", "/", "*", "%",
        "(", ")", "{", "}", "[", "]",
        "<", ">", "=", "!",
        ",", ".", "\"", "\'",
        "|", "&", "^", "~",
        
        "?", ":", ";",
    };
	
    
    /** Regex for matching names */
    public static final Pattern NAME_REGEX = Pattern.compile("[a-zA-Z_\\$][a-zA-Z0-9_\\$]*");
    /** Regex for matching numbers */
    public static final Pattern NUM_REGEX = Pattern.compile("(0x[0-9A-Fa-f]+[lL]?)|(\\d+\\.\\d*[fF]?)|(\\d*\\.\\d+[fF]?)(0x[0-9A-Fa-f]+[lL]?)|(\\d+[lL]?)|(\\d+\\.\\d*[fF]?)|(\\d*\\.\\d+[fF]?)");
    
}
