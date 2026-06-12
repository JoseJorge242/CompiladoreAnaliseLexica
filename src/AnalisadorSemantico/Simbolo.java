package AnalisadorSemantico;

public class Simbolo {
    
private final String nome;
    private final String tipo; // "int", "float", "boolean"
    private final int    linha;
    private final int    coluna;
 
    public Simbolo(String nome, String tipo, int linha, int coluna) {
        this.nome   = nome;
        this.tipo   = tipo;
        this.linha  = linha;
        this.coluna = coluna;
    }
 
    public String getNome()   { return nome;   }
    public String getTipo()   { return tipo;   }
    public int    getLinha()  { return linha;  }
    public int    getColuna() { return coluna; }
 
    @Override
    public String toString() {
        return String.format("Simbolo[nome='%s', tipo='%s', L%d:C%d]",
                             nome, tipo, linha, coluna);
    }
}
