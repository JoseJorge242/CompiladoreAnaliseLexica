package AnalisadorLexico;

public class JavaLex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AnalisadorLexico lex = new AnalisadorLexico(args[0]);
		
		Token t=null;
		
		while((t= lex.proximoToken())!=TipoToken.EOF) {
			System.out.println(t);
		}
	}

}
