package AnalisadorSemantico;

public class ErroSemantico extends RuntimeException {

    private int linha;
    private int coluna;

    public ErroSemantico(int linha, int coluna, String mensagem) {
        super(formatarMensagem( coluna, linha , mensagem));
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getLinha() { return linha;  }
    public int getColuna() { return coluna; }

    private static String formatarMensagem(int linha, int coluna, String msg) {
        return "[Linha " + linha + ", Col " + coluna + "] ERRO SEMÂNTICO: " + msg;
    }
}