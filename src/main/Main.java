package main;
 
import java.io.FileNotFoundException;
import java.util.List;
 
import AnalisadorLexico.AnalisadorLexico;
import AnalisadorLexico.LeitorArquivosTexto;
import AnalisadorSemantico.AnalisadorSemantico;
import AnalisadorSintatico.No;
import AnalisadorSintatico.Parser;
 
public class Main {
 
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("       INICIANDO COMPILACAO              ");
        System.out.println("=========================================");
 
        try (LeitorArquivosTexto leitor = new LeitorArquivosTexto("codigo.txt")) {
 
            AnalisadorLexico lexer = new AnalisadorLexico(leitor);
 
            System.out.println("\n[Sintatico] Iniciando processamento gramatical...");
            Parser parser = new Parser(lexer);
            No.Programa arvore = parser.programa();
            System.out.println("[Sintatico] Analise sintatica concluida sem erros.");
 
            System.out.println("\n[Semantico] Iniciando verificacao semantica...");
            AnalisadorSemantico semantico = new AnalisadorSemantico();
            List<String> erros = semantico.analisar(arvore);
 
            if (erros.isEmpty()) {
                System.out.println("[Semantico] Nenhum erro semantico encontrado.");
            } else {
                System.out.println("[Semantico] Erros encontrados:");
                for (String erro : erros) {
                    System.err.println("  " + erro);
                }
            }
 
            System.out.println("\n=========================================");
            if (erros.isEmpty()) {
                System.out.println("     COMPILACAO CONCLUIDA COM SUCESSO    ");
            } else {
                System.out.println("     COMPILACAO CONCLUIDA COM ERROS      ");
            }
            System.out.println("=========================================");
 
        } catch (FileNotFoundException e) {
            System.err.println("ERRO: O arquivo 'codigo.txt' nao foi encontrado.");
        } catch (Exception e) {
            System.err.println("ERRO INESPERADO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
