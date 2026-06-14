package main;
 
import java.io.FileNotFoundException;
import java.util.List;

import analisadorLexico.AnalisadorLexico;
import analisadorLexico.LeitorArquivosTexto;
import analisadorSemantico.AnalisadorSemantico;
import analisadorSintatico.No;
import analisadorSintatico.Parser;
import geradorCodigo.GeradorByteCode;
import geradorCodigo.GeradorCodigoIntermediario;
import geradorCodigo.MaquinaVirtual;
 
public class Main {
	public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("       INICIANDO COMPILAÇÃO              ");
        System.out.println("=========================================");

        try (LeitorArquivosTexto leitor = new LeitorArquivosTexto("codigo.txt")) {
            AnalisadorLexico lexer = new AnalisadorLexico(leitor);
            Parser parser = new Parser(lexer);

            System.out.println("\n--- [A e B] ANÁLISE LÉXICA E SINTÁTICA ---");
            No.Programa arvore = parser.programa();

            if (arvore != null) {
                System.out.println("Árvore de Sintaxe Abstrata (AST) construída com SUCESSO!");

                System.out.println("\n--- [C] ANÁLISE SEMÂNTICA ---");
                AnalisadorSemantico semantico = new AnalisadorSemantico();
                List<String> erros = semantico.analisar(arvore);

                if (erros.isEmpty()) {
                    System.out.println("Análise Semântica concluída sem erros.");
                    
                    // REQUISITO D: GERAÇÃO DE CÓDIGO INTERMEDIÁRIO (TAC)
                    System.out.println("\n--- [D] GERAÇÃO DE CÓDIGO INTERMEDIÁRIO (TAC) ---");
                    GeradorCodigoIntermediario geradorTac = new GeradorCodigoIntermediario();
                    geradorTac.gerar(arvore);
                    List<String> codigoTAC = geradorTac.gerar(arvore);
                    
                    System.out.println("Código Intermediário Gerado (TAC):");
                    for (String instrucao : codigoTAC) {
                        System.out.println("  " + instrucao);
                    }

                    // REQUISITO E: GERAÇÃO DE CÓDIGO FINAL (BYTECODE DA VIRTUAL MACHINE)
                    System.out.println("\n--- [E] GERAÇÃO DE CÓDIGO FINAL (BYTECODE) ---");
                    GeradorByteCode geradorBytecode = new GeradorByteCode();
                    List<GeradorByteCode.InstrucaoVM> bytecode = geradorBytecode.traduzirParaBytecode(codigoTAC);
                    
                    System.out.println("Bytecode Objeto Gerado com Sucesso:");
                    System.out.println("-----------------------------------------");
                    for (int i = 0; i < bytecode.size(); i++) {
                        System.out.printf("%04d: %s\n", i, bytecode.get(i));
                    }
                    System.out.println("-----------------------------------------");

                    System.out.println("\n--- [VM] INICIANDO EXECUÇÃO DO BYTECODE ---");
                    MaquinaVirtual vm = new MaquinaVirtual();
                    vm.executar(bytecode);
                    System.out.println("--- [VM] EXECUÇÃO FINALIZADA COM SUCESSO ---");

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
            System.err.println("ERRO: O arquivo de teste 'codigo.txt' não foi encontrado.");
        } catch (Exception e) {
            System.err.println("ERRO INESPERADO: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=========================================");
        System.out.println("         COMPILAÇÃO CONCLUÍDA            ");
        System.out.println("=========================================");
    }
}
