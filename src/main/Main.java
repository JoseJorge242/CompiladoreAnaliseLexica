package main;
 
import java.io.FileNotFoundException;
import java.util.List;

import AnalisadorLexico.AnalisadorLexico;
import AnalisadorLexico.LeitorArquivosTexto;
import AnalisadorSemantico.AnalisadorSemantico;
import AnalisadorSintatico.No;
import AnalisadorSintatico.Parser;
import geradorCodigo.GeradorByteCode;
import geradorCodigo.GeradorCodigoIntermediario;
import geradorCodigo.MaquinaVirtual;
 
public class Main {
	public static void main(String[] args) {
        System.out.println("------------------------------------------");
        System.out.println("       INICIANDO COMPILAÇÃO              ");
        System.out.println("------------------------------------------");

        try (LeitorArquivosTexto leitor = new LeitorArquivosTexto("codigo.txt")) {
            AnalisadorLexico lexer = new AnalisadorLexico(leitor);
            Parser parser = new Parser(lexer);

            System.out.println("\n---ANÁLISE LÉXICA E SINTÁTICA---");
            No.Programa arvore = parser.programa();

            if (arvore != null) {
                System.out.println("Árvore de Sintaxe Abstrata (AST) construída com SUCESSO!");

                System.out.println("\n---ANÁLISE SEMÂNTICA---");
                AnalisadorSemantico semantico = new AnalisadorSemantico();
                List<String> erros = semantico.analisar(arvore);

                if (erros.isEmpty()) {
                    System.out.println("Análise Semântica concluída sem erros.");
                    
                    System.out.println("\n---GERAÇÃO DE CÓDIGO INTERMEDIÁRIO (TAC)---");
                    GeradorCodigoIntermediario geradorTac = new GeradorCodigoIntermediario();
                    List<String> codigoTAC = geradorTac.gerar(arvore);
                    
                    System.out.println("Código Intermediário Gerado (TAC):");
                    for (String instrucao : codigoTAC) {
                        System.out.println("  " + instrucao);
                    }

                    System.out.println("\n---GERAÇÃO DE CÓDIGO FINAL (BYTECODE)---");
                    GeradorByteCode geradorBytecode = new GeradorByteCode();
                    List<GeradorByteCode.InstrucaoVM> bytecode = geradorBytecode.traduzirParaBytecode(codigoTAC);
                    
                    System.out.println("Bytecode Objeto Gerado com Sucesso:");
                    System.out.println("-----------------------------------------");
                    for (int i = 0; i < bytecode.size(); i++) {
                        System.out.printf("%04d: %s\n", i, bytecode.get(i));
                    }
                    System.out.println("-----------------------------------------");

                    System.out.println("\n---INICIANDO EXECUÇÃO DO BYTECODE---");
                    MaquinaVirtual vm = new MaquinaVirtual();
                    vm.executar(bytecode);
                    System.out.println("---EXECUÇÃO FINALIZADA COM SUCESSO---");

                } else {
                    System.err.println("\nErros Semânticos detectados (" + erros.size() + "):");
                    for (String erro : erros) {
                        System.err.println(" -> " + erro);
                    }
                }
            } else {
                System.err.println("Erro Crítico: Árvore Sintática Vazia.");
            }

        } catch (FileNotFoundException e) {
            System.err.println("ERRO: O arquivo de teste não foi encontrado.");
        } catch (Exception e) {
            System.err.println("ERRO INESPERADO: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n------------------------------------------");
        System.out.println("         COMPILAÇÃO CONCLUÍDA :)           ");
        System.out.println("------------------------------------------");
    }
}
