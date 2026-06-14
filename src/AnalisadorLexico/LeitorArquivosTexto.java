package analisadorLexico;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class LeitorArquivosTexto implements AutoCloseable{
	private final static int TAMANHO_BUFFER=1024;
	private int[] bufferDeLeitura;
	private int ponteiro;
	private int inicioLexema;
	private StringBuilder lexema;
	private InputStream is;
	private int linha;
	private int coluna;
	private int colunaAnterior;
	
	public  LeitorArquivosTexto(String arquivo) throws FileNotFoundException {
		
			this.is= new FileInputStream(arquivo);
			this.bufferDeLeitura = new int[TAMANHO_BUFFER *2];
			this.lexema= new StringBuilder();
			this.ponteiro=0;
			this.inicioLexema=0;
			this.linha=1;
			this.coluna=1;
			
			recarregarBloco(0);
			recarregarBloco(1);
	}

	private void recarregarBloco(int bloco) {
		int indiceInicial= bloco*TAMANHO_BUFFER;
		int indiceFinal= indiceInicial+TAMANHO_BUFFER;
		
		for(int i= indiceInicial; i<indiceFinal;i++) {
			try {
				int caractere= is.read();
				bufferDeLeitura[i]=caractere;
				
				if(caractere==-1) {
					Arrays.fill(bufferDeLeitura, i, indiceFinal, -1);
					break;
				}
				
			} catch (IOException e) {
				throw new RuntimeException("Erro ao ler o código fonte.", e);
			}
		}
	}
	  private void incrementarPonteiro() {
	        ponteiro++;
	 
	        if (ponteiro == TAMANHO_BUFFER * 2) {
	            ponteiro = 0;
	        }
	        if (ponteiro == TAMANHO_BUFFER) {
	            if (inicioLexema >= TAMANHO_BUFFER) {
	                recarregarBloco(0);
	            }
	        } else if (ponteiro == 0) {
	            if (inicioLexema < TAMANHO_BUFFER) {
	                recarregarBloco(1);
	            }
	        }
	    }
	private int lerCaracterDoBuffer(){
		int ret= bufferDeLeitura[ponteiro];
		incrementarPonteiro();
		return ret;
	}
	public int lerProximoCaractere() {
		int c= lerCaracterDoBuffer();
		if(c==-1) {
			return -1;
		}
		lexema.append((char)c);
		colunaAnterior=coluna;
		if(c=='\n') {
			linha++;
			coluna=1;
		}else {
			coluna++;
		}
		return c;
	}
	public void retroceder() {
		if(lexema.length()==0) {
			return;
		}
		ponteiro--;
		char caractereRemovido = lexema.charAt(lexema.length() - 1);
        lexema.deleteCharAt(lexema.length() - 1);
		if(ponteiro<0) {
			ponteiro= (TAMANHO_BUFFER*2)-1;
		}
		if (caractereRemovido == '\n') {
            linha--;
            coluna = colunaAnterior;
        } else {
            coluna--;
        }
	}
	public void zera() {
		ponteiro=inicioLexema;
		lexema.setLength(0);
	}
	 public void confirmar() {
	        inicioLexema = ponteiro;
	        lexema.setLength(0);
	        if (inicioLexema == TAMANHO_BUFFER) {
	            recarregarBloco(0);
	        } else if (inicioLexema == 0 && ponteiro == 0) {
	            recarregarBloco(1);
	        }
	    }
	public String getLexema() {
		return lexema.toString();
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}

	@Override
	public void close() throws Exception {
		if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }		
	}
}
