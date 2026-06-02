package AnalisadorLexico;

public class AnalisadorLexico {

	LeitorArquivosTexto latd;

	public AnalisadorLexico(LeitorArquivosTexto latd) {
		super();
		this.latd = latd;
	}
	
	public Token proximoToken() {
		int caracterlido= -1;
		
		
		while((caracterlido= latd.lerProximoCaractere())!=-1) {
			char c= (char)caracterlido;
			
			if(c== ' '|| c=='\n' || c=='\r' || c=='\t') {
				continue;
			} else if(c == ':' || c == ';' || c == ',' || c == '(' || c == ')' || c == '{' || c == '}') {
				return delimitador(c);
			}else if(c == '*' || c == '/' || c == '%' || c == '+' || c == '-') {
				return operadorAritmetico(c);
			}else if(c == '<' || c == '>' || c == '=' || c == '!' || c == '&' || c == '|') {
				return operadorRelacional(c);
			}else if(Character.isDigit(c)) {
				return caracteresNumericos(c);
			}else if(Character.isLetter(c)|| c=='_') {
				return identificador(c);
			}
			return new Token(TipoToken.ERROR, Character.toString(c));

		}
		return new Token(TipoToken.EOF, "EOF");
	}
	private Token identificador(char c) {
		StringBuilder sb = new StringBuilder();
		sb.append(c);
		
		int proximo;
		while ((proximo = latd.lerProximoCaractere()) != -1) {
			char pc = (char) proximo;
			if (Character.isLetterOrDigit(pc) || pc == '_') {
				sb.append(pc);
			} else {
				latd.retroceder();
				break;
			}
		}
		
		String texto = sb.toString();
				switch (texto) {
			case "if":       return new Token(TipoToken.IF, texto);
			case "else":     return new Token(TipoToken.ELSE, texto);
			case "while":    return new Token(TipoToken.WHILE, texto);
			case "for":      return new Token(TipoToken.FOR, texto);
			case "return":   return new Token(TipoToken.RETURN, texto);
			case "do":       return new Token(TipoToken.DO, texto);
			case "break":    return new Token(TipoToken.BREAK, texto);
			case "continue": return new Token(TipoToken.CONTINUE, texto);
			case "int":      return new Token(TipoToken.INT, texto);
			case "float":    return new Token(TipoToken.FLOAT, texto);
			case "string":   return new Token(TipoToken.STRING, texto);
			case "boolean":  return new Token(TipoToken.BOOLEAN, texto);
			case "true":     return new Token(TipoToken.TRUE, texto);
			case "false":    return new Token(TipoToken.FALSE, texto);
			default:         return new Token(TipoToken.IDENTIFIER, texto);
		}		
	}

	private Token caracteresNumericos(char c) {
		StringBuilder sb = new StringBuilder();
		sb.append(c);
		
		int proximo;
		while ((proximo = latd.lerProximoCaractere()) != -1) {
			char pc = (char) proximo;
			if (Character.isDigit(pc)) {
				sb.append(pc);
			} else {
				latd.retroceder();
				break;
			}
		}
		return new Token(TipoToken.INT_LITERAL, sb.toString());
	}

	public Token operadorAritmetico(char c) {
		 if(c=='*') {
			return new Token(TipoToken.MULT, "*");
		}else if(c=='/') {
			return new Token(TipoToken.DIV, "/");
		}else if(c=='%') {
			return new Token(TipoToken.MOD, "%");
		}
		else if(c=='+') {
			c=(char) latd.lerProximoCaractere();
			if(c=='+') {
				return new Token(TipoToken.INCREMET, "++");
			}else if(c=='=') {
				return new Token(TipoToken.ASSING, "+=");
			}else {
				latd.retroceder();
				return new Token(TipoToken.PLUS, "+");	
			}
		}else if(c=='-') {
			int proximo = latd.lerProximoCaractere();
	        if ((char) proximo == '-') return new Token(TipoToken.DECREMENT, "--");
	        if ((char) proximo == '=') return new Token(TipoToken.SUB_ASSING, "-=");
	        latd.retroceder();
	        return new Token(TipoToken.MINUS, "-");
		}
		return null;
	}
	
	public Token delimitador(char c) {
		if (c == ':') return new Token(TipoToken.COLON, ":");
		if (c == ';') return new Token(TipoToken.SEMICOLON, ";");
		if (c == ',') return new Token(TipoToken.COMMA, ",");
		if (c == '(') return new Token(TipoToken.LPAREN, "(");
		if (c == ')') return new Token(TipoToken.RPAREN, ")");
		if (c == '{') return new Token(TipoToken.LBRACE, "{");
		if (c == '}') return new Token(TipoToken.RBRACE, "}");
		return null;
	}
	public Token operadorRelacional(char c) {

		if(c=='<') {
			c= (char) latd.lerProximoCaractere();
			if(c== '=') {
				return new Token(TipoToken.LESS_EQUALS, "<=");
			}else {
				latd.retroceder();
				return new Token(TipoToken.LESS, "<");
			}
		}else if(c=='>') {
			c= (char) latd.lerProximoCaractere();
			if(c== '=') {
				return new Token(TipoToken.GREATER_EQUALS, ">=");
			}else {
				latd.retroceder();
				return new Token(TipoToken.GREATER, ">");
			}
		}else if (c == '=') {
	        int proximo = latd.lerProximoCaractere();
	        if ((char) proximo == '=') {
	            return new Token(TipoToken.EQUAL, "==");
	        }
	        latd.retroceder(); 
	        return new Token(TipoToken.ASSING, "=");
		} else if (c == '!') {
			int proximo = latd.lerProximoCaractere();
			if ((char) proximo == '=') return new Token(TipoToken.NOT_EQUAL, "!=");
			latd.retroceder();
			return new Token(TipoToken.NOT, "!");
		}else if (c == '&') {
			int proximo = latd.lerProximoCaractere();
			if ((char) proximo == '&') return new Token(TipoToken.AND, "&&");
			latd.retroceder();
			return new Token(TipoToken.ERROR, "&");
		}else if (c == '|') {
			int proximo = latd.lerProximoCaractere();
			if ((char) proximo == '|') return new Token(TipoToken.OR, "||");
			latd.retroceder();
			return new Token(TipoToken.ERROR, "|");
		}
		return null;
	}
}
