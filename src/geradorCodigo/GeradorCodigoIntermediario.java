package geradorCodigo;

import java.util.ArrayList;
import java.util.List;

import analisadorSintatico.No;

public class GeradorCodigoIntermediario {
	
	private final List<String> codigoInstrucoes = new ArrayList<>();
    private int contadorTemporarios = 0;
    private int contadorRotulos = 0;

    public List<String> gerar(No.Programa programa) {
        codigoInstrucoes.clear();
        contadorTemporarios = 0;
        contadorRotulos = 0;

        if (programa.getInstrucoes() != null) {
            for (No instrucao : programa.getInstrucoes()) {
                gerarInstrucao(instrucao);
            }
        }
        return codigoInstrucoes;
    }

    private void emitir(String instrucao) {
        codigoInstrucoes.add(instrucao);
    }

    private String novoTemporario() {
        return "t" + (contadorTemporarios++);
    }

    private String novoRotulo() {
        return "L" + (contadorRotulos++);
    }

    private void gerarInstrucao(No no) {
        if (no == null) return;

        if (no instanceof No.Bloco) {
            No.Bloco bloco = (No.Bloco) no;
            for (No inst : bloco.getInstrucoes()) {
                gerarInstrucao(inst);
            }
        } 
        
        else if (no instanceof No.DeclaracaoVariavel) {
            No.DeclaracaoVariavel dec = (No.DeclaracaoVariavel) no;
            if (dec.getInicializador() != null) {
                String temp = gerarExpressao(dec.getInicializador());
                emitir(dec.getNome() + " = " + temp);
            }
        } 
        
        else if (no instanceof No.Atribuicao) {
            No.Atribuicao atrib = (No.Atribuicao) no;
            String temp = gerarExpressao(atrib.getValor());
            
            if (atrib.getOperador().equals("=")) {
                emitir(atrib.getNome() + " = " + temp); 
            } else {
                String opSimplificado = atrib.getOperador().replace("=", "");
                String tempOp = novoTemporario();
                emitir(tempOp + " = " + atrib.getNome() + " " + opSimplificado + " " + temp);
                emitir(atrib.getNome() + " = " + tempOp);
            }
        } 
        
        else if (no instanceof No.IncrDecr) {
            No.IncrDecr id = (No.IncrDecr) no;
            String op = id.getOperador().equals("++") ? "+" : "-";
            String temp = novoTemporario();
            emitir(temp + " = " + id.getNome() + " " + op + " 1");
            emitir(id.getNome() + " = " + temp);
        } 
        
        else if (no instanceof No.Se) {
            No.Se seNode = (No.Se) no;
            String rotuloSenao = novoRotulo();
            String rotuloFim = novoRotulo();

            String cond = gerarExpressao(seNode.getCondicao());
            emitir("ifFalse " + cond + " goto " + rotuloSenao);
            
            gerarInstrucao(seNode.getEntao());
            emitir("goto " + rotuloFim);
            
            emitir(rotuloSenao + ":");
            if (seNode.getSenao() != null) {
                gerarInstrucao(seNode.getSenao());
            }
            emitir(rotuloFim + ":");
        } 
        
        else if (no instanceof No.Enquanto) {
            No.Enquanto enquantoNode = (No.Enquanto) no;
            String rotuloInicio = novoRotulo();
            String rotuloFim = novoRotulo();

            emitir(rotuloInicio + ":");
            String cond = gerarExpressao(enquantoNode.getCondicao());
            emitir("ifFalse " + cond + " goto " + rotuloFim);
            
            gerarInstrucao(enquantoNode.getCorpo());
            emitir("goto " + rotuloInicio);
            emitir(rotuloFim + ":");
        } 
        
        else if (no instanceof No.Para) {
            No.Para paraNode = (No.Para) no;
            String rotuloInicio = novoRotulo();
            String rotuloFim = novoRotulo();

            gerarInstrucao(paraNode.getInicializacao());
            
            emitir(rotuloInicio + ":");
            String cond = gerarExpressao(paraNode.getCondicao());
            emitir("ifFalse " + cond + " goto " + rotuloFim);
            
            gerarInstrucao(paraNode.getCorpo());
            gerarInstrucao(paraNode.getIncremento());
            emitir("goto " + rotuloInicio);
            
            emitir(rotuloFim + ":");
        }
        else if (no instanceof No.Retorno) {
            No.Retorno ret = (No.Retorno) no;
            String temp = gerarExpressao(ret.getValor());
            emitir("print " + temp);
        }
    }
    
    private String gerarExpressao(No expr) {
        if (expr == null) return "0";

        if (expr instanceof No.Literal) {
            return ((No.Literal) expr).getValor();
        } 
        
        else if (expr instanceof No.Identificador) {
            return ((No.Identificador) expr).getNome();
        } 
        
        else if (expr instanceof No.Binario) {
            No.Binario bin = (No.Binario) expr;
            String esq = gerarExpressao(bin.getEsquerda());
            String dir = gerarExpressao(bin.getDireita());
            String temp = novoTemporario();
            emitir(temp + " = " + esq + " " + bin.getOperador() + " " + dir);
            return temp;
        } 
        
        else if (expr instanceof No.Unario) {
            No.Unario un = (No.Unario) expr;
            String operando = gerarExpressao(un.getOperando());
            String temp = novoTemporario();
            emitir(temp + " = " + un.getOperador() + operando);
            return temp;
        }

        return "0";
    }
}