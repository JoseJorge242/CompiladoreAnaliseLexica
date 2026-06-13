package AnalisadorSintatico;
 
import java.util.List;
 
public abstract class No {
 
private int linha;
private int coluna;
 
    protected No(int coluna, int linha) {
        this.linha = linha;
        this.coluna = coluna;
    }
 
    public int getLinha(){ 
        return linha;  }
    public int getColuna(){ 
        return coluna; }
 
    public static class Programa extends No {
        private List<No> instrucoes;
        public Programa(List<No> instrucoes) {
            super(0,  0);
            this.instrucoes= instrucoes;
        }
        public List<No> getInstrucoes(){ 
            return instrucoes; }
    }
 
    public static class Bloco extends No {
        private List<No> instrucoes;
        public Bloco(int linha,int coluna,List<No> instrucoes) {
            super(linha, coluna);
            this.instrucoes = instrucoes;
        }
        public List<No> getInstrucoes(){ 
            return instrucoes; }
    }
 
    public static class DeclaracaoVariavel extends No {
        private String tipo;
        private String nome;
        private No inicializador;
 
        public DeclaracaoVariavel( int coluna, int linha, String tipo, String nome, No inicializador) {
            super(linha,coluna);
            this.tipo = tipo;
            this.nome = nome;
            this.inicializador = inicializador;
        }
 
        public String getTipo() {
            return tipo;}
        public String getNome(){ 
            return nome;}
        public No getInicializador() { 
            return inicializador;}
    }
 
    public static class Atribuicao extends No {
        private String nome;
        private String operador;
        private  No valor;
 
        public Atribuicao(No valor, int coluna, int linha , String nome , String operador) {
            super(linha, coluna);
             this.valor = valor;
            this.nome = nome;
            this.operador = operador;
           
        }
 
        public String getNome() { 
            return nome;}
        public String getOperador() {
             return operador; }
        public No getValor(){ 
            return valor;}
    }
 
    public static class IncrDecr extends No {
        private String nome;
        private String operador;
 
        public IncrDecr( String nome,int coluna, String operador, int linha) {
            super(linha, coluna);
          this.operador = operador;
             this.nome = nome;
        }
 
        public String getNome(){ 
            return nome;}
        public String getOperador() { 
            return operador;}
    }
 
    public static class Se extends No {
        private No condicao;
        private Bloco entao;
        private Bloco senao;
 
        public Se(int linha,No condicao, Bloco entao, Bloco senao, int coluna) {
            super(linha, coluna);
            this.condicao = condicao;
            this.entao = entao;
            this.senao = senao;
        }
 
        public No getCondicao() { 
            return condicao;}
        public Bloco getEntao() { 
            return entao;}
        public Bloco getSenao() { 
            return senao;}
    }
    public static class Enquanto extends No {
        private No condicao;
        private Bloco corpo;
 
        public Enquanto(int linha, No condicao, Bloco corpo, int coluna) {
            super(linha, coluna);
            this.condicao = condicao;
            this.corpo = corpo;
        }
 
        public No getCondicao() { 
            return condicao;}
        public Bloco getCorpo() { 
            return corpo;}
    }
 
    public static class Para extends No {
        private No init;
        private No condicao;
        private No incremento;
        private Bloco corpo;
 
        public Para(int linha, int coluna, No condicao, No incremento,No init, Bloco corpo) {
            super(linha, coluna);
            this.init = init;
            this.condicao = condicao;
            this.incremento = incremento;
            this.corpo = corpo;
        }
 
        public No getInit() { 
            return init;}
        public No getCondicao() { 
            return condicao;}
        public No getIncremento() { 
            return incremento;}
        public Bloco getCorpo(){ 
            return corpo;}
    }
 
    public static class Retorno extends No {
        private No valor;
 
        public Retorno( int coluna, int linha, No valor) {
            super(linha, coluna);
            this.valor = valor;
        }
 
        public No getValor() { 
            return valor;}
    }
 
    public static class Binario extends No {
        private String operador;
        private No esquerda;
        private No direita;
 
        public Binario( int coluna,int linha, String operador, No esquerda, No direita) {
            super(linha, coluna);
            this.operador = operador;
            this.esquerda = esquerda;
            this.direita = direita;
        }
 
        public String getOperador() {
            return operador;}
        public No getEsquerda() { 
            return esquerda;}
        public No getDireita() { 
            return direita;}
    }
 
    public static class Unario extends No {
        private String operador;
        private No operando;
 
        public Unario(int linha, int coluna, String operador, No operando) {
            super(linha, coluna);
            this.operador = operador;
            this.operando = operando;
        }
 
        public String getOperador() {
             return operador; }
        public No getOperando() {
             return operando; }
    }
 
    public static class Identificador extends No {
        private final String nome;
 
        public Identificador(int linha, int coluna, String nome) {
            super(linha, coluna);
            this.nome = nome;
        }
 
        public String getNome() {
             return nome; }
    }
 
    public static class Literal extends No {
        private String tipo;
        private String valor;
 
        public Literal( int coluna, String tipo, String valor, int linha) {
            super(linha, coluna);
            this.tipo = tipo;
            this.valor = valor;
        }
 
        public String getTipo(){
             return tipo;}
        public String getValor(){
             return valor; }
    }
}