package AnalisadorSintatico;

import java.util.ArrayList;
import java.util.List;

import AnalisadorLexico.AnalisadorLexico;
import AnalisadorLexico.TipoToken;
import AnalisadorLexico.Token;

public class Parser {

	List<Token> bufferTokens;
	private final static int TAMANHO_BUFFER=1;
	AnalisadorLexico lex;
	boolean chegouNoFim= false;
	
	public Parser(AnalisadorLexico lex) {
		this.lex=lex;
		bufferTokens= new ArrayList<>();
		lerToken();
	}

	private void lerToken() {
		if(bufferTokens.size()>0) {
			bufferTokens.remove(0);
		}
		while(bufferTokens.size()< TAMANHO_BUFFER &&! chegouNoFim) {
		Token proximo = lex.proximoToken();
		bufferTokens.add(proximo);
		if(proximo.getTipo()== TipoToken.EOF) {
			chegouNoFim=true;
		}
		System.out.println("Lido "+lookaHead(1));
		}
	}
	Token lookaHead(int k) {
		if(bufferTokens.isEmpty()) {
			return null;
		}
		if(k-1>= bufferTokens.size()) {
			return bufferTokens.get(bufferTokens.size()-1);
		}
		return bufferTokens.get(k-1);
	}
	void match(TipoToken tipo) {
		if(lookaHead(1).getTipo()==tipo) {
			System.out.println("Match: "+ lookaHead(1));
			lerToken();
		}else {
			erroSintatico(tipo.toString());
		}
	}

	 void erroSintatico(String... tokensEperados ) {
		String mensagem="Erro sintática: esperando um dos seguintes (";
		for(int i=0; i<tokensEperados.length; i++) {
			mensagem+= tokensEperados[i];
			if(i<tokensEperados.length-1) {
				mensagem+=",";
			}
		}
		mensagem+="), mas foi encontrado"+ lookaHead(1);
		throw new RuntimeException(mensagem);
	}
	 public void programa() {
		 match(TipoToken.COLON);
		 match(TipoToken.DECLARATIONS);
		 listaDedeclaracoes();
		 match(TipoToken.COLON);
		 match(TipoToken.ALGORITHM);
		 listaComandos();
		 match(TipoToken.EOF);
	 }

	 void listaDedeclaracoes() {
		 if(lookaHead(1).getTipo()== TipoToken.IDENTIFIER) {
			 declaracao();
			 listaDedeclaracoes();
		 }	 
	 }
	 void declaracao() {
		 match(TipoToken.IDENTIFIER);
		 match(TipoToken.COLON);
		 tipoVar();
	 }

	 void tipoVar() {
		 if(lookaHead(1).getTipo()==TipoToken.INT) {
			 match(TipoToken.INT);
		 } else if(lookaHead(1).getTipo()== TipoToken.FLOAT) {
		 	match(TipoToken.FLOAT);
		 } else if (lookaHead(1).getTipo() == TipoToken.STRING) {
		        match(TipoToken.STRING);
		    } else if (lookaHead(1).getTipo() == TipoToken.BOOLEAN) {
		        match(TipoToken.BOOLEAN);
		 }else {
		 erroSintatico("INT","FLOAT", "STRING", "BOOLEAN");
	 	} 
	 }
	 void expressaoAritmetica() {
			 termoAritmetico();
			 expressaoAritmetica2();	 
		
		 
	 }

	  void expressaoAritmetica2() {
		  if(lookaHead(1).getTipo()== TipoToken.PLUS || lookaHead(1).getTipo()== TipoToken.MINUS){
			  expressaoAritmetica2SubRegra1();
			  expressaoAritmetica2();
		  } else {
			  
		  }
		  
	 }

	   void expressaoAritmetica2SubRegra1() {
		   if(lookaHead(1).getTipo()== TipoToken.PLUS) {
			   match(TipoToken.PLUS);
			   termoAritmetico();
		   }else if(lookaHead(1).getTipo()== TipoToken.MINUS) {
			   match(TipoToken.MINUS);
				   termoAritmetico();  
			 }else {
				   erroSintatico("+","-");
			   }
		   
		  
	  }
	   void termoAritmetico() {
		    fatorAritmetico();
		    
		    while (lookaHead(1).getTipo() == TipoToken.MULT || 
		           lookaHead(1).getTipo() == TipoToken.DIV || 
		           lookaHead(1).getTipo() == TipoToken.MOD) {
		        
		        TipoToken tipo = lookaHead(1).getTipo();
		        
		        if (tipo == TipoToken.MULT) {
		            match(TipoToken.MULT);
		        } else if (tipo == TipoToken.DIV) {
		            match(TipoToken.DIV);
		        } else if (tipo == TipoToken.MOD) {
		            match(TipoToken.MOD);
		        }
		        
		        fatorAritmetico();
		    }
		}

		  void termoAritmetico2() {
			  if(lookaHead(1).getTipo()== TipoToken.MULT || lookaHead(1).getTipo()== TipoToken.DIV){
				  termoAritmetico2SubRegra1();
				  termoAritmetico2();
			  } else {
				  
			  }
			  
		 }

		   void termoAritmetico2SubRegra1() {
			   if(lookaHead(1).getTipo()== TipoToken.MULT) {
				   match(TipoToken.MULT);
				   fatorAritmetico();
			   }else if(lookaHead(1).getTipo()== TipoToken.DIV) {
				   match(TipoToken.DIV);
					   fatorAritmetico();
				 }else {
					   erroSintatico("*","/");
				 }		  
		  }
		  void fatorAritmetico() {
			  if(lookaHead(1).getTipo()==TipoToken.INT_LITERAL) {
				  match(TipoToken.INT_LITERAL);
			  } else if (lookaHead(1).getTipo() == TipoToken.FLOAT_LITERAL) { 
			        match(TipoToken.FLOAT_LITERAL);
			    } else if (lookaHead(1).getTipo() == TipoToken.STRING_LITERAL) { 
			        match(TipoToken.STRING_LITERAL);
			    } else if (lookaHead(1).getTipo() == TipoToken.TRUE) {   
			        match(TipoToken.TRUE);
			    } else if (lookaHead(1).getTipo() == TipoToken.FALSE) {    
			        match(TipoToken.FALSE);
			    }else if(lookaHead(1).getTipo()==TipoToken.IDENTIFIER) {
				  match(TipoToken.IDENTIFIER);
			  } else if(lookaHead(1).getTipo()==TipoToken.LPAREN) {
				  match(TipoToken.LPAREN);
				  expressaoAritmetica();
				  match(TipoToken.RPAREN);
			  } else {
				erroSintatico("INT_LITERAL", "FLOAT_LITERAL", "STRING_LITERAL", "IDENTIFIER", "TRUE", "FALSE", "(");
		  }
		}
		  void expressaoRelacional() {
				 termoRelacional();
				 expressaoRelacional2();	 
			
			 
		 }

		  void expressaoRelacional2() {
			  if(lookaHead(1).getTipo()== TipoToken.AND || lookaHead(1).getTipo()== TipoToken.OR){
				  operadorBooleano();
				  termoRelacional();
				  expressaoRelacional2();
			  } else {
				  
			  } 
		  }
		  void termoRelacional() {
			  if(lookaHead(1).getTipo()== TipoToken.INT_LITERAL
					  || lookaHead(1).getTipo()==TipoToken.FLOAT_LITERAL
					  || lookaHead(1).getTipo()==TipoToken.IDENTIFIER
					  || lookaHead(1).getTipo()==TipoToken.TRUE
					  || lookaHead(1).getTipo()==TipoToken.FALSE
					  || lookaHead(1).getTipo()==TipoToken.STRING_LITERAL
					  || lookaHead(1).getTipo()==TipoToken.LPAREN){
				  
				expressaoAritmetica();
				opReal();
				expressaoAritmetica();
			  }else {
				  erroSintatico("INT_LITERAL","FLOAT_LITERAL","IDENTIFIER","TRUE","FALSE","(");
			  }
		  }
		  void opReal() {
			    TipoToken tipo = lookaHead(1).getTipo();

			    if (tipo == TipoToken.NOT_EQUAL) {
			        match(TipoToken.NOT_EQUAL); 
			    } else if (tipo == TipoToken.EQUAL) {
			        match(TipoToken.EQUAL);
			    } else if (tipo == TipoToken.GREATER_EQUALS) {
			        match(TipoToken.GREATER_EQUALS);
			    } else if (tipo == TipoToken.GREATER) {
			        match(TipoToken.GREATER);
			    } else if (tipo == TipoToken.LESS) {
			        match(TipoToken.LESS);
			    } else if (tipo == TipoToken.LESS_EQUALS) {
			        match(TipoToken.LESS_EQUALS);
			    } else {

			        erroSintatico("!=", "==", ">", ">=", "<", "<=");
			    }
			}
		
		void operadorBooleano() {
			if(lookaHead(1).getTipo()== TipoToken.AND) {
				match(TipoToken.AND);
			}else if(lookaHead(1).getTipo()==TipoToken.OR) {
				match(TipoToken.OR);
			}else {
				erroSintatico("AND","OR");
			}
		}
		
		void listaComandos() {
			comando();
			listarComandarSubRegra1();
		}
		void listarComandarSubRegra1() {
			if(lookaHead(1).getTipo()==TipoToken.ASSIGN_KW
					|| lookaHead(1).getTipo()== TipoToken.READ
					|| lookaHead(1).getTipo()== TipoToken.PRINT
					|| lookaHead(1).getTipo()== TipoToken.IF
					|| lookaHead(1).getTipo()== TipoToken.WHILE
					|| lookaHead(1).getTipo()== TipoToken.BEGIN) {
				listaComandos();
			}else {	}
		}
		void comandoAtribuicao() {
			match(TipoToken.ASSIGN_KW);
			expressaoAritmetica();
			match(TipoToken.TO);
			match(TipoToken.IDENTIFIER);
		}
		void comando() {
			if(lookaHead(1).getTipo()== TipoToken.ASSIGN_KW) {
				comandoAtribuicao();
			} else if(lookaHead(1).getTipo()== TipoToken.READ) {
				comandoEntrada();
			} else if(lookaHead(1).getTipo()== TipoToken.PRINT) {
				comandoSaida();
			} else if(lookaHead(1).getTipo()== TipoToken.IF) {
				comandoCondicao();
			} else if(lookaHead(1).getTipo()== TipoToken.WHILE) {
				comandoRepeticao();
			} else if(lookaHead(1).getTipo()== TipoToken.BEGIN) {
				subAlgoritmo();
			}else {
				erroSintatico("ASSIGN_KW","READ","PRINT","IF","WHILE","BEGIN");
			}
		}
		void comandoEntrada() {
			match(TipoToken.READ);
			match(TipoToken.IDENTIFIER);
		}
		void comandoSaida() {
			match(TipoToken.PRINT);
			expressaoAritmetica();
		}


		void comandoCondicao() {
		    match(TipoToken.IF);
		    expressaoRelacional(); 
		    match(TipoToken.THEN);
		    comando();
		    
		    if (lookaHead(1).getTipo() == TipoToken.ELSE) {
		        match(TipoToken.ELSE);
		        comando();
		    }
		}
		void comandoCondicaoSubRegra1() {
			if(lookaHead(1).getTipo()==TipoToken.ELSE) {
				match(TipoToken.ELSE);
				comando();
			}else {
				
			}
		}
		void comandoRepeticao() {
			match(TipoToken.WHILE);
			expressaoRelacional();
			if (lookaHead(1).getTipo() == TipoToken.LBRACE) {
				match(TipoToken.LBRACE);
				listaComandos();
				match(TipoToken.RBRACE);
			} else {
				comando();
			}
		}
		void subAlgoritmo() {
			match(TipoToken.BEGIN);
			listaComandos();
			match(TipoToken.EOF);
		}	
}
