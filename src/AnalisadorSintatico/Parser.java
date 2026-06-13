package AnalisadorSintatico;
 
import java.util.ArrayList;
import java.util.List;
 
import AnalisadorLexico.AnalisadorLexico;
import AnalisadorLexico.TipoToken;
import AnalisadorLexico.Token;
 
public class Parser {
 
    List<Token> bufferTokens;
    private final static int TAMANHO_BUFFER = 1;
    AnalisadorLexico lex;
    boolean chegouNoFim = false;
 
    public Parser(AnalisadorLexico lex) {
        this.lex = lex;
        bufferTokens = new ArrayList<>();
        lerToken();
    }
 
    private void lerToken() {
        if (bufferTokens.size() > 0) {
            bufferTokens.remove(0);
        }
        while (bufferTokens.size() < TAMANHO_BUFFER && !chegouNoFim) {
            Token proximo = lex.proximoToken();
            bufferTokens.add(proximo);
            if (proximo.getTipo() == TipoToken.EOF) {
                chegouNoFim = true;
            }
            System.out.println("Lido " + lookaHead(1));
        }
    }
 
    Token lookaHead(int k) {
        if (bufferTokens.isEmpty()) {
            return null;
        }
        if (k - 1 >= bufferTokens.size()) {
            return bufferTokens.get(bufferTokens.size() - 1);
        }
        return bufferTokens.get(k - 1);
    }
 
    Token match(TipoToken tipo) {
        if (lookaHead(1).getTipo() == tipo) {
            Token t = lookaHead(1);
            System.out.println("Match: " + t);
            lerToken();
            return t;
        } else {
            erroSintatico(tipo.toString());
            return null;
        }
    }
 
    void erroSintatico(String... tokensEsperados) {
        String mensagem = "Erro sintatico: esperando um dos seguintes (";
        for (int i = 0; i < tokensEsperados.length; i++) {
            mensagem += tokensEsperados[i];
            if (i < tokensEsperados.length - 1) {
                mensagem += ",";
            }
        }
        mensagem += "), mas foi encontrado " + lookaHead(1);
        throw new RuntimeException(mensagem);
    }
 
    public No.Programa programa() {
        int linha = lookaHead(1).getLinha();
        int coluna = lookaHead(1).getColuna();
 
        match(TipoToken.COLON);
        match(TipoToken.DECLARATIONS);
 
        List<No> instrucoes = new ArrayList<>();
        listaDedeclaracoes(instrucoes);
 
        match(TipoToken.COLON);
        match(TipoToken.ALGORITHM);
 
        listaComandos(instrucoes);
 
        match(TipoToken.EOF);
 
        return new No.Programa(instrucoes, linha, coluna);
    }
 
    void listaDedeclaracoes(List<No> lista) {
        if (lookaHead(1).getTipo() == TipoToken.IDENTIFIER) {
            lista.add(declaracao());
            listaDedeclaracoes(lista);
        }
    }
 
    No.DeclaracaoVariavel declaracao() {
        Token id = match(TipoToken.IDENTIFIER);
        match(TipoToken.COLON);
        String tipo = tipoVar();
        return new No.DeclaracaoVariavel(id.getLexema(), tipo, null, id.getLinha(), id.getColuna());
    }
 
    String tipoVar() {
        TipoToken t = lookaHead(1).getTipo();
        if (t == TipoToken.INT) {
            match(TipoToken.INT);
            return "int";
        } else if (t == TipoToken.FLOAT) {
            match(TipoToken.FLOAT);
            return "float";
        } else if (t == TipoToken.STRING) {
            match(TipoToken.STRING);
            return "string";
        } else if (t == TipoToken.BOOLEAN) {
            match(TipoToken.BOOLEAN);
            return "boolean";
        } else {
            erroSintatico("INT", "FLOAT", "STRING", "BOOLEAN");
            return null;
        }
    }
 
    void listaComandos(List<No> lista) {
        lista.add(comando());
        listarComandarSubRegra1(lista);
    }
 
    void listarComandarSubRegra1(List<No> lista) {
        TipoToken t = lookaHead(1).getTipo();
        if (t == TipoToken.ASSIGN_KW || t == TipoToken.READ
                || t == TipoToken.PRINT || t == TipoToken.IF
                || t == TipoToken.WHILE || t == TipoToken.BEGIN) {
            listaComandos(lista);
        }
    }
 
    No comando() {
        TipoToken t = lookaHead(1).getTipo();
        if (t == TipoToken.ASSIGN_KW) {
            return comandoAtribuicao();
        } else if (t == TipoToken.READ) {
            return comandoEntrada();
        } else if (t == TipoToken.PRINT) {
            return comandoSaida();
        } else if (t == TipoToken.IF) {
            return comandoCondicao();
        } else if (t == TipoToken.WHILE) {
            return comandoRepeticao();
        } else if (t == TipoToken.BEGIN) {
            return subAlgoritmo();
        } else {
            erroSintatico("ASSIGN_KW", "READ", "PRINT", "IF", "WHILE", "BEGIN");
            return null;
        }
    }
 
    No.Atribuicao comandoAtribuicao() {
        Token kw = match(TipoToken.ASSIGN_KW);
        No valor = expressaoAritmetica();
        match(TipoToken.TO);
        Token id = match(TipoToken.IDENTIFIER);
        return new No.Atribuicao(id.getLexema(), "=", valor, kw.getLinha(), kw.getColuna());
    }
 
    No comandoEntrada() {
    Token kw = match(TipoToken.READ);
    Token id = match(TipoToken.IDENTIFIER);
    return new No.Atribuicao(id.getLexema(), "=",
            new No.Identificador(id.getLexema(), id.getLinha(), id.getColuna()), // próprio identificador
            kw.getLinha(), kw.getColuna());
}
 
    No comandoSaida() {
        match(TipoToken.PRINT);
        return expressaoAritmetica();
    }
 
    No.Se comandoCondicao() {
        Token kw = match(TipoToken.IF);
        No condicao = expressaoRelacional();
        match(TipoToken.THEN);
 
        List<No> entaoLista = new ArrayList<>();
        entaoLista.add(comando());
        No.Bloco entao = new No.Bloco(entaoLista, kw.getLinha(), kw.getColuna());
 
        No.Bloco senao = null;
        if (lookaHead(1).getTipo() == TipoToken.ELSE) {
            match(TipoToken.ELSE);
            List<No> senaoLista = new ArrayList<>();
            senaoLista.add(comando());
            senao = new No.Bloco(senaoLista, kw.getLinha(), kw.getColuna());
        }
 
        return new No.Se(condicao, entao, senao, kw.getLinha(), kw.getColuna());
    }
 
    void comandoCondicaoSubRegra1() {
        if (lookaHead(1).getTipo() == TipoToken.ELSE) {
            match(TipoToken.ELSE);
            comando();
        }
    }
 
    No.Enquanto comandoRepeticao() {
        Token kw = match(TipoToken.WHILE);
        No condicao = expressaoRelacional();
 
        List<No> corpoLista = new ArrayList<>();
        if (lookaHead(1).getTipo() == TipoToken.LBRACE) {
            match(TipoToken.LBRACE);
            listaComandos(corpoLista);
            match(TipoToken.RBRACE);
        } else {
            corpoLista.add(comando());
        }
 
        No.Bloco corpo = new No.Bloco(corpoLista, kw.getLinha(), kw.getColuna());
        return new No.Enquanto(condicao, corpo, kw.getLinha(), kw.getColuna());
    }
 
    No.Bloco subAlgoritmo() {
        Token kw = match(TipoToken.BEGIN);
        List<No> lista = new ArrayList<>();
        listaComandos(lista);
        match(TipoToken.EOF);
        return new No.Bloco(lista, kw.getLinha(), kw.getColuna());
    }
 
    No expressaoAritmetica() {
        No esquerda = termoAritmetico();
        return expressaoAritmetica2(esquerda);
    }
 
    No expressaoAritmetica2(No esquerda) {
        TipoToken t = lookaHead(1).getTipo();
        if (t == TipoToken.PLUS || t == TipoToken.MINUS) {
            Token op = match(t);
            No direita = termoAritmetico();
            No binario = new No.Binario(op.getLexema(), esquerda, direita, op.getLinha(), op.getColuna());
            return expressaoAritmetica2(binario);
        }
        return esquerda;
    }
 
    No termoAritmetico() {
        No esquerda = fatorAritmetico();
        while (lookaHead(1).getTipo() == TipoToken.MULT
                || lookaHead(1).getTipo() == TipoToken.DIV
                || lookaHead(1).getTipo() == TipoToken.MOD) {
            Token op = match(lookaHead(1).getTipo());
            No direita = fatorAritmetico();
            esquerda = new No.Binario(op.getLexema(), esquerda, direita, op.getLinha(), op.getColuna());
        }
        return esquerda;
    }
 
    No fatorAritmetico() {
        Token t = lookaHead(1);
 
        if (t.getTipo() == TipoToken.INT_LITERAL) {
            match(TipoToken.INT_LITERAL);
            return new No.Literal("int", t.getLexema(), t.getLinha(), t.getColuna());
 
        } else if (t.getTipo() == TipoToken.FLOAT_LITERAL) {
            match(TipoToken.FLOAT_LITERAL);
            return new No.Literal("float", t.getLexema(), t.getLinha(), t.getColuna());
 
        } else if (t.getTipo() == TipoToken.STRING_LITERAL) {
            match(TipoToken.STRING_LITERAL);
            return new No.Literal("string", t.getLexema(), t.getLinha(), t.getColuna());
 
        } else if (t.getTipo() == TipoToken.TRUE) {
            match(TipoToken.TRUE);
            return new No.Literal("boolean", "true", t.getLinha(), t.getColuna());
 
        } else if (t.getTipo() == TipoToken.FALSE) {
            match(TipoToken.FALSE);
            return new No.Literal("boolean", "false", t.getLinha(), t.getColuna());
 
        } else if (t.getTipo() == TipoToken.IDENTIFIER) {
            match(TipoToken.IDENTIFIER);
            return new No.Identificador(t.getLexema(), t.getLinha(), t.getColuna());
 
        } else if (t.getTipo() == TipoToken.LPAREN) {
            match(TipoToken.LPAREN);
            No expr = expressaoAritmetica();
            match(TipoToken.RPAREN);
            return expr;
 
        } else {
            erroSintatico("INT_LITERAL", "FLOAT_LITERAL", "STRING_LITERAL", "IDENTIFIER", "TRUE", "FALSE", "(");
            return null;
        }
    }
 
    No expressaoRelacional() {
        No esquerda = termoRelacional();
        return expressaoRelacional2(esquerda);
    }
 
    No expressaoRelacional2(No esquerda) {
        TipoToken t = lookaHead(1).getTipo();
        if (t == TipoToken.AND || t == TipoToken.OR) {
            Token op = match(t);
            No direita = termoRelacional();
            No binario = new No.Binario(op.getLexema(), esquerda, direita, op.getLinha(), op.getColuna());
            return expressaoRelacional2(binario);
        }
        return esquerda;
    }
 
    No termoRelacional() {
        TipoToken t = lookaHead(1).getTipo();
        if (t == TipoToken.INT_LITERAL || t == TipoToken.FLOAT_LITERAL
                || t == TipoToken.IDENTIFIER || t == TipoToken.TRUE
                || t == TipoToken.FALSE || t == TipoToken.STRING_LITERAL
                || t == TipoToken.LPAREN) {
            No esquerda = expressaoAritmetica();
            Token op = opReal();
            No direita = expressaoAritmetica();
            return new No.Binario(op.getLexema(), esquerda, direita, op.getLinha(), op.getColuna());
        }
        erroSintatico("INT_LITERAL", "FLOAT_LITERAL", "IDENTIFIER", "TRUE", "FALSE", "(");
        return null;
    }
 
    Token opReal() {
        TipoToken t = lookaHead(1).getTipo();
        if (t == TipoToken.NOT_EQUAL) {
            return match(TipoToken.NOT_EQUAL);
        } else if (t == TipoToken.EQUAL) {
            return match(TipoToken.EQUAL);
        } else if (t == TipoToken.GREATER_EQUALS) {
            return match(TipoToken.GREATER_EQUALS);
        } else if (t == TipoToken.GREATER) {
            return match(TipoToken.GREATER);
        } else if (t == TipoToken.LESS) {
            return match(TipoToken.LESS);
        } else if (t == TipoToken.LESS_EQUALS) {
            return match(TipoToken.LESS_EQUALS);
        } else {
            erroSintatico("!=", "==", ">", ">=", "<", "<=");
            return null;
        }
    }
 
    void operadorBooleano() {
        if (lookaHead(1).getTipo() == TipoToken.AND) {
            match(TipoToken.AND);
        } else if (lookaHead(1).getTipo() == TipoToken.OR) {
            match(TipoToken.OR);
        } else {
            erroSintatico("AND", "OR");
        }
    }
 
    void termoAritmetico2() {
        if (lookaHead(1).getTipo() == TipoToken.MULT || lookaHead(1).getTipo() == TipoToken.DIV) {
            termoAritmetico2SubRegra1();
            termoAritmetico2();
        }
    }
 
    void termoAritmetico2SubRegra1() {
        if (lookaHead(1).getTipo() == TipoToken.MULT) {
            match(TipoToken.MULT);
            fatorAritmetico();
        } else if (lookaHead(1).getTipo() == TipoToken.DIV) {
            match(TipoToken.DIV);
            fatorAritmetico();
        } else {
            erroSintatico("*", "/");
        }
    }
}