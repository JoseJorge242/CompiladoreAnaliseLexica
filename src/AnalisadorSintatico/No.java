package AnalisadorSintatico;
 
import java.util.List;
 
public abstract class No {
 
    private int linha;
    private int coluna;
 
    public No(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }
 
    public int getLinha() {
        return linha;
    }
 
    public int getColuna() {
        return coluna;
    }
 
    public static class Programa extends No {
        private List<No> instrucoes;
 
        public Programa(List<No> instrucoes, int linha, int coluna) {
            super(linha, coluna);
            this.instrucoes = instrucoes;
        }
 
        public List<No> getInstrucoes() {
            return instrucoes;
        }
    }
 
    public static class Bloco extends No {
        private List<No> instrucoes;
 
        public Bloco(List<No> instrucoes, int linha, int coluna) {
            super(linha, coluna);
            this.instrucoes = instrucoes;
        }
 
        public List<No> getInstrucoes() {
            return instrucoes;
        }
    }
 
    public static class DeclaracaoVariavel extends No {
        private String nome;
        private String tipo;
        private No inicializador;
 
        public DeclaracaoVariavel(String nome, String tipo, No inicializador, int linha, int coluna) {
            super(linha, coluna);
            this.nome = nome;
            this.tipo = tipo;
            this.inicializador = inicializador;
        }
 
        public String getNome() {
            return nome;
        }
 
        public String getTipo() {
            return tipo;
        }
 
        public No getInicializador() {
            return inicializador;
        }
    }
 
    public static class Atribuicao extends No {
        private String nome;
        private String operador;
        private No valor;
 
        public Atribuicao(String nome, String operador, No valor, int linha, int coluna) {
            super(linha, coluna);
            this.nome = nome;
            this.operador = operador;
            this.valor = valor;
        }
 
        public String getNome() {
            return nome;
        }
 
        public String getOperador() {
            return operador;
        }
 
        public No getValor() {
            return valor;
        }
    }
 
    public static class IncrDecr extends No {
        private String nome;
        private String operador;
 
        public IncrDecr(String nome, String operador, int linha, int coluna) {
            super(linha, coluna);
            this.nome = nome;
            this.operador = operador;
        }
 
        public String getNome() {
            return nome;
        }
 
        public String getOperador() {
            return operador;
        }
    }
 
    public static class Se extends No {
        private No condicao;
        private Bloco entao;
        private Bloco senao;
 
        public Se(No condicao, Bloco entao, Bloco senao, int linha, int coluna) {
            super(linha, coluna);
            this.condicao = condicao;
            this.entao = entao;
            this.senao = senao;
        }
 
        public No getCondicao() {
            return condicao;
        }
 
        public Bloco getEntao() {
            return entao;
        }
 
        public Bloco getSenao() {
            return senao;
        }
    }
 
    public static class Enquanto extends No {
        private No condicao;
        private Bloco corpo;
 
        public Enquanto(No condicao, Bloco corpo, int linha, int coluna) {
            super(linha, coluna);
            this.condicao = condicao;
            this.corpo = corpo;
        }
 
        public No getCondicao() {
            return condicao;
        }
 
        public Bloco getCorpo() {
            return corpo;
        }
    }
 
    public static class Para extends No {
        private No inicializacao;
        private No condicao;
        private No incremento;
        private Bloco corpo;
 
        public Para(No inicializacao, No condicao, No incremento, Bloco corpo, int linha, int coluna) {
            super(linha, coluna);
            this.inicializacao = inicializacao;
            this.condicao = condicao;
            this.incremento = incremento;
            this.corpo = corpo;
        }
 
        public No getInicializacao() {
            return inicializacao;
        }
 
        public No getCondicao() {
            return condicao;
        }
 
        public No getIncremento() {
            return incremento;
        }
 
        public Bloco getCorpo() {
            return corpo;
        }
    }
 
    public static class Retorno extends No {
        private No valor;
 
        public Retorno(No valor, int linha, int coluna) {
            super(linha, coluna);
            this.valor = valor;
        }
 
        public No getValor() {
            return valor;
        }
    }
 
    public static class Binario extends No {
        private String operador;
        private No esquerda;
        private No direita;
 
        public Binario(String operador, No esquerda, No direita, int linha, int coluna) {
            super(linha, coluna);
            this.operador = operador;
            this.esquerda = esquerda;
            this.direita = direita;
        }
 
        public String getOperador() {
            return operador;
        }
 
        public No getEsquerda() {
            return esquerda;
        }
 
        public No getDireita() {
            return direita;
        }
    }
 
    public static class Unario extends No {
        private String operador;
        private No operando;
 
        public Unario(String operador, No operando, int linha, int coluna) {
            super(linha, coluna);
            this.operador = operador;
            this.operando = operando;
        }
 
        public String getOperador() {
            return operador;
        }
 
        public No getOperando() {
            return operando;
        }
    }
 
    public static class Identificador extends No {
        private String nome;
 
        public Identificador(String nome, int linha, int coluna) {
            super(linha, coluna);
            this.nome = nome;
        }
 
        public String getNome() {
            return nome;
        }
    }
 
    public static class Literal extends No {
        private String tipo;
        private String valor;
 
        public Literal(String tipo, String valor, int linha, int coluna) {
            super(linha, coluna);
            this.tipo = tipo;
            this.valor = valor;
        }
 
        public String getTipo() {
            return tipo;
        }
 
        public String getValor() {
            return valor;
        }
    }
}