package AnalisadorLexico;

import java.io.FileNotFoundException;

public class JavaLex {
    public static void main(String[] args) {
        
        System.out.println("=========================================");
        System.out.println("       INICIANDO ANÁLISE LÉXICA          ");
        System.out.println("=========================================");
        
        try (LeitorArquivosTexto leitor = new LeitorArquivosTexto("codigo.txt")) {
            
            AnalisadorLexico lexer = new AnalisadorLexico(leitor);
            Token token;
            
            do {
                token = lexer.proximoToken();
                
                if (token != null) {
                    if (token.getTipo() == TipoToken.ERROR) {
                        System.err.println("ERRO LÉXICO: " + token);
                    } else {
                        System.out.println(token);
                    }
                }
                
            } while (token != null && token.getTipo() != TipoToken.EOF);
            
        } catch (FileNotFoundException e) {
            System.err.println("ERRO CRÍTICO: O arquivo 'codigo.txt' não foi encontrado no diretório do projeto.");
        } catch (Exception e) {
            System.err.println("ERRO INESPERADO: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=========================================");
        System.out.println("         ANÁLISE CONCLUÍDA               ");
        System.out.println("=========================================");
    }
}