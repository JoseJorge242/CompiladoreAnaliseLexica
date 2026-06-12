package main;

import java.io.FileNotFoundException;

import AnalisadorLexico.AnalisadorLexico;
import AnalisadorLexico.LeitorArquivosTexto;
import AnalisadorLexico.Token;
import AnalisadorSintatico.Parser;

public class Main {

	public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("       INICIANDO COMPILAÇÃO          ");
        System.out.println("=========================================");
        
        try (LeitorArquivosTexto leitor = new LeitorArquivosTexto("codigo.txt")) {
            
            AnalisadorLexico lexer = new AnalisadorLexico(leitor);
            
            Parser parser = new Parser(lexer);
            Token token;
            
            System.out.println("\n[Sintático] Iniciando processamento gramatical...");
         
            parser.programa();
            
            System.out.println("=========================================");
            System.out.println("         ANÁLISE CONCLUÍDA               ");
            System.out.println("=========================================");
        } catch (FileNotFoundException e) {
            System.err.println("ERRO CRÍTICO: O arquivo 'codigo.txt' não foi encontrado no diretório do projeto.");
        } catch (Exception e) {
            System.err.println("ERRO INESPERADO: " + e.getMessage());
            e.printStackTrace();
        }
	}

}
