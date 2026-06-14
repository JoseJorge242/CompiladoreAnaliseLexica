package AnalisadorLexico;


public class AnalisadorLexico {

    private LeitorArquivosTexto latd;

    public AnalisadorLexico(LeitorArquivosTexto latd) {
        this.latd = latd;
    }
    
    public Token proximoToken() {
        int caracterLido;
        
        while ((caracterLido = latd.lerProximoCaractere()) != -1) {
            char c = (char) caracterLido;
            
            if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                latd.confirmar(); 
                continue;
            }
            
            int linhaToken = latd.getLinha();
            int colunaToken = latd.getColuna() - 1;
            
            if (c == ':' || c == ';' || c == ',' || c == '(' || c == ')' || c == '{' || c == '}') {
                return delimitador(c, linhaToken, colunaToken);
            }
            if (c == '*' || c == '%' || c == '+' || c == '-') {
                return operadorAritmeticoOuComentario(c, linhaToken, colunaToken);
            }
            if(c=='/') {
            		Token t=	comentarioOuDivisao(c, linhaToken, colunaToken);
            		if(t==null) continue;
            		return t;
            }
            if (c == '<' || c == '>' || c == '=' || c == '!' || c == '&' || c == '|') {
                return operadorRelacionalOuLogico(c, linhaToken, colunaToken);
            }
            if (Character.isDigit(c)) {
                return caracteresNumericos(linhaToken, colunaToken);
            }
            if (Character.isLetter(c) || c == '_') {
                return identificador(linhaToken, colunaToken);
            }
            if (c == '"') {
                return literalString(linhaToken, colunaToken);
            }
            latd.confirmar();
            return new Token(TipoToken.ERROR, Character.toString(c), linhaToken, colunaToken);
        }
                return new Token(TipoToken.EOF, "EOF", latd.getLinha(), latd.getColuna());
    }
    
    private Token literalString(int linha, int coluna) {
        int c;
        boolean fechou = false;
        int anterior = -1;
        while ((c = latd.lerProximoCaractere()) != -1) {
            if ((char) c == '"' && anterior != '\\') {
                fechou = true;
                break;
            }
            anterior = c;
        }
        String texto = latd.getLexema();
        latd.confirmar();
        if (!fechou) {
            return new Token(TipoToken.ERROR, texto, linha, coluna);
        }
        return new Token(TipoToken.STRING_LITERAL, texto, linha, coluna);
    }

    private Token comentarioOuDivisao(char c, int linha, int coluna) {
        int proximo = latd.lerProximoCaractere();

        if (proximo == '/') { 
            int lido;
            while ((lido = latd.lerProximoCaractere()) != -1) {
                if ((char) lido == '\n') {
                    break;
                }
            }
            latd.confirmar();
            return null; 
        }

        if (proximo == '*') {
            int anterior = -1;
            int ch;
            boolean fechou = false;
            
            while ((ch = latd.lerProximoCaractere()) != -1) {
                if (anterior == '*' && (char) ch == '/') {
                    fechou = true;
                    break;
                }
                anterior = ch;
            }
            
            String texto = latd.getLexema();
            latd.confirmar();
            
            if (!fechou) {
                return new Token(TipoToken.ERROR, texto, linha, coluna);
            }
            return null;
        }

        if (proximo != -1) {
            latd.retroceder();
        }
        latd.confirmar();
        return new Token(TipoToken.DIV, "/", linha, coluna);
    }

	private Token identificador(int linha, int coluna) {
        int proximo;
        while ((proximo = latd.lerProximoCaractere()) != -1) {
            char pc = (char) proximo;
            if (!(Character.isLetterOrDigit(pc) || pc == '_')) {
                latd.retroceder();
                break;
            }
        }
        
        String texto = latd.getLexema();
        latd.confirmar(); 
 
        switch (texto.toLowerCase()) {
            case "if": return new Token(TipoToken.IF, texto, linha, coluna);
            case "else": return new Token(TipoToken.ELSE, texto, linha, coluna);
            case "while": return new Token(TipoToken.WHILE, texto, linha, coluna);
            case "for": return new Token(TipoToken.FOR, texto, linha, coluna);
            case "return": return new Token(TipoToken.RETURN, texto, linha, coluna);
            case "do": return new Token(TipoToken.DO, texto, linha, coluna);
            case "break": return new Token(TipoToken.BREAK, texto, linha, coluna);
            case "continue": return new Token(TipoToken.CONTINUE, texto, linha, coluna);
            case "int": return new Token(TipoToken.INT, texto, linha, coluna);
            case "float": return new Token(TipoToken.FLOAT, texto, linha, coluna);
            case "string": return new Token(TipoToken.STRING, texto, linha, coluna);
            case "boolean": return new Token(TipoToken.BOOLEAN, texto, linha, coluna);
            case "true": return new Token(TipoToken.TRUE, texto, linha, coluna);
            case "false": return new Token(TipoToken.FALSE, texto, linha, coluna);
            case "declarations": return new Token(TipoToken.DECLARATIONS, texto, linha, coluna);
            case "algorithm": return new Token(TipoToken.ALGORITHM, texto, linha, coluna);
            case "assign": return new Token(TipoToken.ASSIGN_KW, texto, linha, coluna);
            case "read": return new Token(TipoToken.READ, texto, linha, coluna);
            case "print": return new Token(TipoToken.PRINT, texto, linha, coluna);
            case "then": return new Token(TipoToken.THEN, texto, linha, coluna);
            case "to": return new Token(TipoToken.TO, texto, linha, coluna);
            case "begin": return new Token(TipoToken.BEGIN, texto, linha, coluna);
            default: return new Token(TipoToken.IDENTIFIER, texto, linha, coluna);
        }        
    }

    private Token caracteresNumericos(int linha, int coluna) {
        int proximo;
        boolean ehFloat = false;
        
        while ((proximo = latd.lerProximoCaractere()) != -1) {
            char pc = (char) proximo;
            if (Character.isDigit(pc)) {
                continue;
            } else if (pc == '.' && !ehFloat) {
                int lookahead = latd.lerProximoCaractere();
                if (lookahead != -1 && Character.isDigit((char) lookahead)) {
                    ehFloat = true;
                } else {
                    if (lookahead != -1) latd.retroceder();
                    latd.retroceder();
                    break;
                }
            } else {
                latd.retroceder();
                break;
            }
        }
        
        String texto = latd.getLexema();
        latd.confirmar();
        if (ehFloat) {
            return new Token(TipoToken.FLOAT_LITERAL, texto, linha, coluna);
        } else {
            return new Token(TipoToken.INT_LITERAL, texto, linha, coluna);
        }
    }

    private Token operadorAritmeticoOuComentario(char c, int linha, int coluna) {
        if (c == '*') { latd.confirmar(); return new Token(TipoToken.MULT, "*", linha, coluna); }
        if (c == '%') { latd.confirmar(); return new Token(TipoToken.MOD, "%", linha, coluna); }
        
        if (c == '+') {
            int proximo = latd.lerProximoCaractere();
            if (proximo == '+') { latd.confirmar(); return new Token(TipoToken.INCREMENT, "++", linha, coluna); }
            if (proximo == '=') { latd.confirmar(); return new Token(TipoToken.ADD_ASSIGN, "+=", linha, coluna); }
            
            if (proximo != -1) latd.retroceder(); 
            latd.confirmar();
            return new Token(TipoToken.PLUS, "+", linha, coluna);
        }
        
        if (c == '-') {
            int proximo = latd.lerProximoCaractere();
            if (proximo == '-') { latd.confirmar(); return new Token(TipoToken.DECREMENT, "--", linha, coluna); }
            if (proximo == '=') { latd.confirmar(); return new Token(TipoToken.SUB_ASSIGN, "-=", linha, coluna); }
            
            if (proximo != -1) latd.retroceder();
            latd.confirmar();
            return new Token(TipoToken.MINUS, "-", linha, coluna);
        }
        return null;
    }
    
    private Token delimitador(char c, int linha, int coluna) {
        latd.confirmar();
        switch (c) {
            case ':': return new Token(TipoToken.COLON, ":", linha, coluna);
            case ';': return new Token(TipoToken.SEMICOLON, ";", linha, coluna);
            case ',': return new Token(TipoToken.COMMA, ",",  linha, coluna);
            case '(': return new Token(TipoToken.LPAREN, "(", linha, coluna);
            case ')': return new Token(TipoToken.RPAREN, ")", linha, coluna);
            case '{': return new Token(TipoToken.LBRACE, "{", linha, coluna);
            case '}': return new Token(TipoToken.RBRACE, "}", linha, coluna);
            default: return null;
        }
    }

    private Token operadorRelacionalOuLogico(char c, int linha, int coluna) {
        if (c == '<') {
            int proximo = latd.lerProximoCaractere();
            if (proximo == '=') { latd.confirmar(); return new Token(TipoToken.LESS_EQUALS, "<=", linha, coluna); }
            if (proximo != -1) latd.retroceder();
            latd.confirmar();
            return new Token(TipoToken.LESS, "<", linha, coluna);
        } 
        if (c == '>') {
            int proximo = latd.lerProximoCaractere();
            if (proximo == '=') { latd.confirmar(); return new Token(TipoToken.GREATER_EQUALS, ">=", linha, coluna); }
            if (proximo != -1) latd.retroceder();
            latd.confirmar();
            return new Token(TipoToken.GREATER, ">", linha, coluna);
        } 
        if (c == '=') {
            int proximo = latd.lerProximoCaractere();
            if (proximo == '=') { latd.confirmar(); return new Token(TipoToken.EQUAL, "==", linha, coluna); }
            if (proximo != -1) latd.retroceder();
            latd.confirmar(); 
            return new Token(TipoToken.ASSIGN, "=", linha, coluna);
        } 
        if (c == '!') {
            int proximo = latd.lerProximoCaractere();
            if (proximo == '=') { latd.confirmar(); return new Token(TipoToken.NOT_EQUAL, "!=", linha, coluna); }
            if (proximo != -1) latd.retroceder();
            latd.confirmar();
            return new Token(TipoToken.NOT, "!", linha, coluna);
        }
        if (c == '&') {
            int proximo = latd.lerProximoCaractere();
            if (proximo == '&') { latd.confirmar(); return new Token(TipoToken.AND, "&&", linha, coluna); }
            if (proximo != -1) latd.retroceder();
            latd.confirmar();
            return new Token(TipoToken.ERROR, "&", linha, coluna);
        }
        if (c == '|') {
            int proximo = latd.lerProximoCaractere();
            if (proximo == '|') { latd.confirmar(); return new Token(TipoToken.OR, "||", linha, coluna); }
            if (proximo != -1) latd.retroceder();
            latd.confirmar();
            return new Token(TipoToken.ERROR, "|", linha, coluna);
        }
        return null;
    }
}