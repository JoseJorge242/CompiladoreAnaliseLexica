package analisadorSemantico;

import java.util.ArrayList;
import java.util.List;

import analisadorSintatico.No;

public class AnalisadorSemantico {

    private TabelaDeSimbolos tabela = new TabelaDeSimbolos();
    private List<String>     erros  = new ArrayList<>();

    public List<String> analisar(No.Programa programa) {
        tabela.entrarEscopo();
        for (No instrucao : programa.getInstrucoes()) {
            analisarInstrucao(instrucao);
        }
        tabela.sairEscopo();
        return erros;
    }

    private void analisarInstrucao(No no) {
        try {
            if (no instanceof No.DeclaracaoVariavel) analisarDeclaracao ((No.DeclaracaoVariavel) no);
            else if (no instanceof No.Atribuicao) analisarAtribuicao ((No.Atribuicao) no);
            else if (no instanceof No.IncrDecr) analisarIncrDecr ((No.IncrDecr) no);
            else if (no instanceof No.Se) analisarSe ((No.Se)no);
            else if (no instanceof No.Enquanto) analisarEnquanto ((No.Enquanto) no);
            else if (no instanceof No.Para) analisarPara ((No.Para) no);
            else if (no instanceof No.Retorno) analisarRetorno ((No.Retorno) no);
            else if (no instanceof No.Bloco) analisarBlocoSozinho((No.Bloco) no);
            else inferirTipoSeguro (no);
        } catch (ErroSemantico e) {
            erros.add(e.getMessage());
        }
    }

    private void analisarDeclaracao(No.DeclaracaoVariavel n) {
        try {
            tabela.declarar(new Simbolo(n.getNome(), n.getTipo(), n.getLinha(), n.getColuna()));
        } catch (ErroSemantico e) {
            erros.add(e.getMessage());
            return;
        }

        if (n.getInicializador() != null) {
            String tipoInit = inferirTipoSeguro(n.getInicializador());
            if (!compativel(n.getTipo(), tipoInit)) {
                erros.add(erro(n.getLinha(), n.getColuna(),
                    "Tipo incompatível na declaração de '" + n.getNome()
                    + "': esperado '" + n.getTipo() + "', obtido '" + tipoInit + "'"));
            }
        }
    }

    private void analisarAtribuicao(No.Atribuicao n) {
        Simbolo sim = tabela.buscar(n.getNome());
        if (sim == null) {
            erros.add(erro(n.getLinha(), n.getColuna(),
                "Variável '" + n.getNome() + "' não foi declarada"));
            return;
        }

        String tipoValor = inferirTipoSeguro(n.getValor());
        String op = n.getOperador();

        if (op.equals("+=") || op.equals("-=")) {
            if (!eNumerico(sim.getTipo())) {
                erros.add(erro(n.getLinha(), n.getColuna(),
                    "Operador '" + op + "' exige variável numérica; '"
                    + n.getNome() + "' é do tipo '" + sim.getTipo() + "'"));
                return;
            }
            if (!eNumerico(tipoValor)) {
                erros.add(erro(n.getLinha(), n.getColuna(),
                    "Operador '" + op + "' exige valor numérico; obtido '" + tipoValor + "'"));
            }
            return;
        }

        if (!compativel(sim.getTipo(), tipoValor)) {
            erros.add(erro(n.getLinha(), n.getColuna(),
                "Tipo incompatível ao atribuir em '" + n.getNome()
                + "': esperado '" + sim.getTipo() + "', obtido '" + tipoValor + "'"));
        }
    }

    private void analisarIncrDecr(No.IncrDecr n) {
        Simbolo sim = tabela.buscar(n.getNome());
        if (sim == null) {
            erros.add(erro(n.getLinha(), n.getColuna(),
                "Variável '" + n.getNome() + "' não foi declarada"));
            return;
        }
        if (!eNumerico(sim.getTipo())) {
            erros.add(erro(n.getLinha(), n.getColuna(),
                "Operador '" + n.getOperador() + "' exige variável numérica; '"
                + n.getNome() + "' é do tipo '" + sim.getTipo() + "'"));
        }
    }

    private void analisarSe(No.Se n) {
        String tipoCond = inferirTipoSeguro(n.getCondicao());
        if (!tipoCond.equals("boolean")) {
            erros.add(erro(n.getLinha(), n.getColuna(),
                "Condição do 'if' deve ser 'boolean', mas é '" + tipoCond + "'"));
        }

        tabela.entrarEscopo();
        analisarBloco(n.getEntao());
        tabela.sairEscopo();

        if (n.getSenao() != null) {
            tabela.entrarEscopo();
            analisarBloco(n.getSenao());
            tabela.sairEscopo();
        }
    }

    private void analisarEnquanto(No.Enquanto n) {
        String tipoCond = inferirTipoSeguro(n.getCondicao());
        if (!tipoCond.equals("boolean")) {
            erros.add(erro(n.getLinha(), n.getColuna(),
                "Condição do 'while' deve ser 'boolean', mas é '" + tipoCond + "'"));
        }

        tabela.entrarEscopo();
        analisarBloco(n.getCorpo());
        tabela.sairEscopo();
    }

    private void analisarPara(No.Para n) {
        tabela.entrarEscopo();

        if (n.getInit() != null)
    analisarInstrucao(n.getInit());

        if (n.getCondicao() != null) {
            String tipoCond = inferirTipoSeguro(n.getCondicao());
            if (!tipoCond.equals("boolean")) {
                erros.add(erro(n.getLinha(), n.getColuna(),
                    "Condição do 'for' deve ser 'boolean', mas é '" + tipoCond + "'"));
            }
        }

        if (n.getIncremento() != null)
            analisarInstrucao(n.getIncremento());

        analisarBloco(n.getCorpo());

        tabela.sairEscopo();
    }

    private void analisarRetorno(No.Retorno n) {
        if (n.getValor() != null) inferirTipoSeguro(n.getValor());
    }

    private void analisarBloco(No.Bloco b) {
        for (No instrucao : b.getInstrucoes())
            analisarInstrucao(instrucao);
    }

    private void analisarBlocoSozinho(No.Bloco b) {
        tabela.entrarEscopo();
        analisarBloco(b);
        tabela.sairEscopo();
    }

    private String inferirTipo(No no) {

        if (no instanceof No.Literal)
            return ((No.Literal) no).getTipo();

        if (no instanceof No.Identificador) {
            String nome = ((No.Identificador) no).getNome();
            Simbolo sim = tabela.buscar(nome);
            if (sim == null)
                throw new ErroSemantico(no.getLinha(), no.getColuna(),
                    "Variável '" + nome + "' não foi declarada");
            return sim.getTipo();
        }

        if (no instanceof No.Binario)
            return inferirBinario((No.Binario) no);

        if (no instanceof No.Unario)
            return inferirUnario((No.Unario) no);

        return "unknown";
    }

    private String inferirTipoSeguro(No no) {
        try {
            return inferirTipo(no);
        } catch (ErroSemantico e) {
            erros.add(e.getMessage());
            return "unknown";
        }
    }

    private String inferirBinario(No.Binario n) {
        String L = inferirTipoSeguro(n.getEsquerda());
        String R = inferirTipoSeguro(n.getDireita());
        String op = n.getOperador();

        if (op.equals("==") || op.equals("!=")
         || op.equals("<")  || op.equals(">")
         || op.equals("<=") || op.equals(">=")) {
            if (!L.equals(R) && !(eNumerico(L) && eNumerico(R))) {
                erros.add(erro(n.getLinha(), n.getColuna(),
                    "Operador '" + op + "' não pode comparar '"
                    + L + "' com '" + R + "'"));
            }
            return "boolean";
        }

        if (op.equals("&&") || op.equals("||")) {
            if (!L.equals("boolean"))
                erros.add(erro(n.getLinha(), n.getColuna(),
                    "Operando esquerdo de '" + op
                    + "' deve ser 'boolean'; obtido '" + L + "'"));
            if (!R.equals("boolean"))
                erros.add(erro(n.getLinha(), n.getColuna(),
                    "Operando direito de '" + op
                    + "' deve ser 'boolean'; obtido '" + R + "'"));
            return "boolean";
        }

        if (op.equals("+") || op.equals("-")
         || op.equals("*") || op.equals("/") || op.equals("%")) {
            if (!eNumerico(L) || !eNumerico(R)) {
                erros.add(erro(n.getLinha(), n.getColuna(),
                    "Operador '" + op + "' exige operandos numéricos; "
                    + "obtido '" + L + "' e '" + R + "'"));
                return "unknown";
            }
            return (L.equals("float") || R.equals("float")) ? "float" : "int";
        }

        return "unknown";
    }

    private String inferirUnario(No.Unario n) {
        String t  = inferirTipoSeguro(n.getOperando());
        String op = n.getOperador();

        if (op.equals("-")) {
            if (!eNumerico(t))
                erros.add(erro(n.getLinha(), n.getColuna(),
                    "Operador '-' unário exige operando numérico; obtido '" + t + "'"));
            return t;
        }
        if (op.equals("!")) {
            if (!t.equals("boolean"))
                erros.add(erro(n.getLinha(), n.getColuna(),
                    "Operador '!' exige operando 'boolean'; obtido '" + t + "'"));
            return "boolean";
        }
        return "unknown";
    }

    private boolean eNumerico(String tipo) {
        return tipo.equals("int") || tipo.equals("float");
    }

    private boolean compativel(String declarado, String atual) {
        if (declarado.equals(atual)) return true;
        if (declarado.equals("float") && atual.equals("int")) return true;
        return false;
    }

    private String erro(int linha, int coluna, String msg) {
        return "[Linha " + linha + ", Col " + coluna + "] ERRO SEMÂNTICO: " + msg;
    }
}