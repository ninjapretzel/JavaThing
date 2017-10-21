import static Constants.JAVA.*;
import java.io.File;
import java.util.Scanner;

/** Contains methods to parse a Java program fed into a Tokenizer */
public class JavaParser {
	
	/** Run location for testing JavaParser */
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
        
        Tokenizer tok = new Tokenizer(src);
        
        tok.reset();
        System.out.println("Ready to parse....");
        
		Node n = JavaParser.parseProgram(tok);
		
		for (String msg : tok.styleProblems) {
			System.out.println(msg);
		}
		
        
    }
	
	/** Parses the root program node from a Tokenizer */
	public static Node parseProgram(Tokenizer tok) {
		Node program = new Node();
		// Java files may have a package / import statements...
		program.mapChild("pack", parsePackage(tok));
		program.mapChild("imports", parseImports(tok));
		
		// Followed by at least one type (class, enum, interface)
		do { program.listChild(parseType(tok)); }
		while (!tok.done());
		
		// Return program node when done
		return program;
	}
	
	/** Parses out a package definition Node, or nothing */
	public static Node parsePackage(Tokenizer tok) {
		// If there is a package definition, read it.
		if (tok.peekToken.is("package")) {
			tok.next(); // consume 'package' token
			
			Token peek = tok.peekToken;
			String packageName = "";
			//Read all names and .s into the package name
			while (peek.is(".", NAME_TYPE)) {
				packageName += peek.content;
				peek = tok.nextPeek(); // Consume the token, move to the next one, and get the peek token.
			}
			tok.requireNext(";"); // Require and consume a ';'
			
			Node pack = new Node();
			pack.mapData("name", packageName);
			return pack;
		}
		// If not, return nothing.
		return null;
	}
	
	/** Parses out import definition Node, may be empty */
	public static Node parseImports(Tokenizer tok) {
		Node imports = new Node();
		
		// Read any import statements...
		while (tok.peekToken.is("import")) {
			tok.next(); // Consume 'import' keyword
			Token peek = tok.peekToken;
			String importName = "";
			// Static imports are a thing
			if (peek.is("static")) {
				importName += "static ";
				peek = tok.nextPeek();
			}
			
			// Read imported package name
			while (peek.is(".", NAME_TYPE)) {
				importName += peek.content;
				peek = tok.nextPeek();
			}
			
			tok.requireNext(";");
			// Add the import to the list
			imports.listData(importName);
		}
		
		return imports;
	}
	
	/** Parses out a type definition, and their bindings */
	public static Node parseType(Tokenizer tok) {
		// Bindings are always in-front of the type.
		Node bindings = parseBindings(tok);
		
		// Then parse the type by itself
		Node type = parseJustType(tok);
		// And attach the bindings to that type
		type.mapChild("bindings", bindings);
		
		return type;
	}
	
	/** Parses out just a class, interface, or enum node*/
	public static Node parseJustType(Tokenizer tok) {
		// At this point, the next token must be one of a type...
		tok.require("class", "interface", "enum");
		
		// Select what kind of type based on what toke is present
		if (tok.peekToken.is("class")) {
			return parseClass(tok);
		} else if (tok.peekToken.is("interface")) {
			return null;
			//return parseInterface(tok);
		} else if (tok.peekToken.is("enum")) {
			return null;
			//return parseEnum(tok);
		}
		
		return null;
	}
	
	/** Parses out a list of bindings */
	public static Node parseBindings(Tokenizer tok) {
		Node bindings = new Node();
		
		Token peek = tok.peekToken;
		// Accept any binding keywords for now
		// Semantics can be  checked later
		while (peek.is("public", "private", "protected", "static", 
				"abstract", "final", "volatile", "synchronized")) {
			bindings.listData(peek);
			peek = tok.nextPeek();
			tok.requireSpaces(1, " Spaces between bindings ");
		}
		
		return bindings;
	}
	
	/** Parses out the innards of a class */
	public static Node parseClass(Tokenizer tok) {
		tok.next(); // consume 'class'
		Node cl = new Node();
		cl.mapData("name", tok.peekToken);
		tok.next(); // Consume type name
		if (tok.peekToken.is("extends")) {
			tok.next(); // Consume 'extends' token
			tok.require(NAME_TYPE);
			cl.mapData("parent", tok.peekToken); // Map name token to parent
		}
		
		tok.requireNewlines(0, "before '{' beginning a type");
		tok.requireNext("{");
		tok.requireNewlines(2, "after '{' beginning a type");
		
		while (!tok.peekToken.is("}")) {
			cl.listChild(parseClassMember(tok));
		}
		
		tok.next(); // consume '}'
		
		return cl;
	}
	
	public static Node parseClassMember(Tokenizer tok) {
		Node member = new Node();
		Node bindings = parseBindings(tok);
		
		if (tok.peekToken.is("class", "interface", "enum")) {
			System.out.println("Detected embedded type");
			member = parseJustType(tok);	
			System.out.println("Finished embedded type: " + member.data.get("name"));
		} else {
			Node code = null;
			Node params = null;
			Token name = null;
			Token type = tok.peekToken;
			tok.next(); // Consume the first type name 
			if (tok.peekToken.is("(")) {
				// We have a constructor!
				// One name (type name) and params list!
				System.out.println("Detected constructor for " + type.content);
				params = parseParamsDecList(tok);
			} else {
				// Otherwise, we have a field, or a method
				// both have Two names (type and actual member name)
				name = tok.peekToken;
				tok.next(); // Consume the second member name 
				
				if (tok.peekToken.is("(")) {
					System.out.println("Detected method " + type.content + " " + name.content);
					// If we have a params list, and two names, we have a method!
					params = parseParamsDecList(tok);
				} else {
					if (tok.peekToken.is("=")) {
						tok.next();
						//Node decAssign = parseExpr(tok);
					}
					// The problem we had that night was this-
					// we were not consuming the ';' after a field declaration
					tok.requireNext(";");
					
					System.out.println("Detected field " + type.content + " " + name.content);
				}
			}
			
			if (params != null) {
				// if (tok.peekToken.is(";")) { }
				code = parseCodeBlock(tok);
			}
			
			member.mapData("type", type);
			member.mapData("name", name);
			member.mapChild("params", params);
			member.mapChild("code", code);
		}
		member.mapChild("bindings", bindings);
		return member;
	}
	
	public static Node parseCodeBlock(Tokenizer tok) {
		Node stmts = new Node();
		tok.requireNext("{");
		
		
		// TBD: Parse actual statments
		
		tok.requireNext("}");
		return stmts;
	}
	
	public static Node parseParamsDecList(Tokenizer tok) {
		tok.requireNext("(");
		
		Node params = new Node();
		while (tok.peekToken.is(NAME_TYPE, ",")) {
			System.out.println("Reading params type" + tok.peekToken);
			if (tok.peekToken.is(",")) { 
				tok.next(); // Consume ','
			}
			Token type = tok.peekToken;
			tok.next(); // Consume type name
			System.out.println("Reading params name" + tok.peekToken);
			Token name = tok.peekToken;
			tok.next(); // Consume var name
			
			Node param = new Node();
			param.mapData("type", type.content);
			param.mapData("name", name.content);
			
			params.listChild(param);
		}
		
		tok.requireNext(")");
		
		return params;
	}
	
}
