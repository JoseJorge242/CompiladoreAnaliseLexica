package geradorCodigo;

public enum Opcode {
	    PUSH,    // Coloca um valor constante no topo da pilha (ex: PUSH 5)
	    LOAD,    // Carrega o valor de uma variável para o topo da pilha (ex: LOAD x)
	    STORE,   // Retira o topo da pilha e armazena em uma variável (ex: STORE x)
	    
	    // Operações Aritméticas
	    ADD,     // Desempilha dois valores, soma, empilha o resultado
	    SUB,     // Desempilha dois valores, subtrai (segundo - primeiro), empilha o resultado
	    MUL,     // Desempilha dois valores, multiplica, empilha o resultado
	    DIV,     // Desempilha dois valores, divide, empilha o resultado
	    MOD,     // Desempilha dois valores, resto da divisão, empilha o resultado
	    
	    // Operações Lógicas e Unárias
	    NEG,     // Inverte o sinal do topo da pilha
	    NOT,     // Inverte o booleano do topo da pilha (0 vira 1, 1 vira 0)
	    AND,     // Operação lógica E bitwise/booleana no topo da pilha
	    OR,      // Operação lógica OU bitwise/booleana no topo da pilha
	    
	    // Operações Relacionais (comparam o topo e empilham 1 para verdadeiro ou 0 para falso)
	    CMP_EQ,  // ==
	    CMP_NE,  // !=
	    CMP_LT,  // <
	    CMP_LE,  // <=
	    CMP_GT,  // >
	    CMP_GE,  // >=
	    
	    // Saltos e Controle de Fluxo
	    LABEL,   // Marcação de destino (não executa nada, serve de referência)
	    JUMP,    // Salto incondicional para uma Label
	    JIF_TRUE,  // Salto se o topo da pilha for verdadeiro (1)
	    JIF_FALSE, // Salto se o topo da pilha for falso (0)
	    
	    // Entrada e Saída
	    READ,    // Lê um valor do teclado e joga na variável indicada
	    PRINT,   // Desempilha o topo da pilha e imprime na tela
	    PRINT_STR, // Imprime uma string literal diretamente
	    
	    HALT     // Encerra a execução do programa
}
