package AnalisadorLexico;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LeitorArquivosTexto {
	private final static int tamanho_buffer=20;
	int[] bufferDeLeitura;
	int ponteiro;
	int bufferAtual;
	int inicioLexema;
	private String lexema;
	InputStream is;
	
	public void leitorArquivoDeTexto(String arquivo) {
		try {
			is= new FileInputStream(arquivo);
			inicializarBuffer();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private void inicializarBuffer(){
		bufferAtual=2;
		inicioLexema=0;
		lexema="";
		bufferDeLeitura= new int [tamanho_buffer*2];
		ponteiro= 0;
		recarregarBuffer1();
	}
	private void recarregarBuffer1() {
		if(bufferAtual==2) {
			bufferAtual=1;
		}
		for(int i=0; i<tamanho_buffer; i++) {
			try {
				bufferDeLeitura[i]= is.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(bufferDeLeitura[i]==-1) {
				break;
			}
		}
		
	}
	private void recarregarBuffer2() {
		if(bufferAtual==1) {
			bufferAtual=2;
		}
		for(int i=tamanho_buffer; i<tamanho_buffer*2; i++) {
			try {
				bufferDeLeitura[i]= is.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(bufferDeLeitura[i]==-1) {
				break;
			}
		}
		
	}
	private void incrementarPonteiro(){
		ponteiro++;
		if(ponteiro== tamanho_buffer) {
			recarregarBuffer2();
		}else if(ponteiro==tamanho_buffer*2) {
			recarregarBuffer1();
			ponteiro=0;
		}
	}
	private int lerCaracterDoBuffer(){
		int ret= bufferDeLeitura[ponteiro];
		System.out.println(this);
		incrementarPonteiro();
		return ret;
	}
	public int lerProximoCaractere() {
		int c= lerCaracterDoBuffer();
		lexema+=(char)c;
		return c;
	}
	public void retroceder() {
		ponteiro--;
		lexema=lexema.substring(0, lexema.length()-1);
		if(ponteiro<0) {
			ponteiro= tamanho_buffer*2-1;
		}
	}
	public void zera() {
		ponteiro=inicioLexema;
		lexema="";
	}
	public void confirmar() {
		inicioLexema=ponteiro;
		lexema="";
	}
	public String getLexema() {
		return lexema;
	}
}
