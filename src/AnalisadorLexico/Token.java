package AnalisadorLexico;

public class Token {

	public TipoToken nome;
	public String Lexema;
	
	public Token(TipoToken nome, String lexema) {
		super();
		this.nome = nome;
		Lexema = lexema;
	}

	@Override
	public String toString() {
		return "<" + nome + "," + Lexema + ">";
	}
	
	
	
}
