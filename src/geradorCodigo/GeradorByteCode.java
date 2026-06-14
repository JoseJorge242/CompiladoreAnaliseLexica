package geradorCodigo;

import java.util.ArrayList;
import java.util.List;

public class GeradorByteCode {
	public static class InstrucaoVM {
        public Opcode opcode;
        public String argumento;

        public InstrucaoVM(Opcode opcode, String argumento) {
            this.opcode = opcode;
            this.argumento = argumento;
        }

        @Override
        public String toString() {
            return opcode + (argumento != null ? " " + argumento : "");
        }
    }

    public List<InstrucaoVM> traduzirParaBytecode(List<String> codigoTAC) {
        List<InstrucaoVM> programaBytecode = new ArrayList<>();

        for (String linha : codigoTAC) {
            linha = linha.trim();
            if (linha.isEmpty()) continue;

            // 1. Definição de Labels
            if (linha.endsWith(":")) {
                String labelNome = linha.substring(0, linha.length() - 1).trim();
                programaBytecode.add(new InstrucaoVM(Opcode.LABEL, labelNome));
                continue;
            }

            // 2. Salto Incondicional (goto)
            if (linha.startsWith("goto ")) {
                String labelDestino = linha.substring(5).trim();
                programaBytecode.add(new InstrucaoVM(Opcode.JUMP, labelDestino));
                continue;
            }

            // 3. Comandos de Entrada e Saída (read / print)
            if (linha.startsWith("print ")) {
                String arg = linha.substring(6).trim();
                if (arg.startsWith("\"")) {
                    programaBytecode.add(new InstrucaoVM(Opcode.PRINT_STR, arg));
                } else {
                    empilharOperando(arg, programaBytecode);
                    programaBytecode.add(new InstrucaoVM(Opcode.PRINT, null));
                }
                continue;
            }

            if (linha.startsWith("read ")) {
                String varNome = linha.substring(5).trim();
                programaBytecode.add(new InstrucaoVM(Opcode.READ, varNome));
                continue;
            }

            // 4. Saltos Condicionais (if / ifFalse)
            if (linha.startsWith("if ") || linha.startsWith("ifFalse ")) {
                boolean isIf = linha.startsWith("if ");
                String[] partes = linha.split(" goto ");
                String labelDestino = partes[1].trim();
                String condicao = partes[0].substring(isIf ? 3 : 8).trim();

                String[] tokens = condicao.split("\\s+");
                
                if (tokens.length == 1) {
                    // Condição simples (ex: if t0 goto L1)
                    empilharOperando(tokens[0], programaBytecode);
                } else if (tokens.length == 3) {
                    // Condição com operador relacional (ex: if a < b goto L1)
                    empilharOperando(tokens[0], programaBytecode);
                    empilharOperando(tokens[2], programaBytecode);
                    
                    switch (tokens[1]) {
                        case "==": programaBytecode.add(new InstrucaoVM(Opcode.CMP_EQ, null)); break;
                        case "!=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_NE, null)); break;
                        case "<": programaBytecode.add(new InstrucaoVM(Opcode.CMP_LT, null)); break;
                        case "<=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_LE, null)); break;
                        case ">": programaBytecode.add(new InstrucaoVM(Opcode.CMP_GT, null)); break;
                        case ">=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_GE, null)); break;
                    }
                }

                if (isIf) {
                    programaBytecode.add(new InstrucaoVM(Opcode.JIF_TRUE, labelDestino));
                } else {
                    programaBytecode.add(new InstrucaoVM(Opcode.JIF_FALSE, labelDestino));
                }
                continue;
            }

            // 5. Atribuições e Expressões (destino = expressao)
            if (linha.contains(" = ")) {
                String[] partes = linha.split(" = ");
                String destino = partes[0].trim();
                String expressao = partes[1].trim();

                String[] tokens = expressao.split("\\s+");

                if (tokens.length == 1) {
                    // Atribuição direta (ex: a = 10 ou a = b)
                    empilharOperando(tokens[0], programaBytecode);
                } 
                else if (tokens.length == 2) {
                    // Operadores unários (ex: -a ou !a)
                    empilharOperando(tokens[1], programaBytecode);
                    if (tokens[0].equals("-")) {
                        programaBytecode.add(new InstrucaoVM(Opcode.NEG, null));
                    } else if (tokens[0].equals("!")) {
                        programaBytecode.add(new InstrucaoVM(Opcode.NOT, null));
                    }
                } 
                else if (tokens.length == 3) {
                    // Operações binárias aritméticas, lógicas ou relacionais (ex: a + b)
                    empilharOperando(tokens[0], programaBytecode);
                    empilharOperando(tokens[2], programaBytecode);

                    switch (tokens[1]) {
                        case "+": programaBytecode.add(new InstrucaoVM(Opcode.ADD, null)); break;
                        case "-": programaBytecode.add(new InstrucaoVM(Opcode.SUB, null)); break;
                        case "*": programaBytecode.add(new InstrucaoVM(Opcode.MUL, null)); break;
                        case "/": programaBytecode.add(new InstrucaoVM(Opcode.DIV, null)); break;
                        case "%": programaBytecode.add(new InstrucaoVM(Opcode.MOD, null)); break;
                        case "&&": programaBytecode.add(new InstrucaoVM(Opcode.AND, null)); break;
                        case "||": programaBytecode.add(new InstrucaoVM(Opcode.OR, null)); break;
                        case "==": programaBytecode.add(new InstrucaoVM(Opcode.CMP_EQ, null)); break;
                        case "!=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_NE, null)); break;
                        case "<": programaBytecode.add(new InstrucaoVM(Opcode.CMP_LT, null)); break;
                        case "<=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_LE, null)); break;
                        case ">": programaBytecode.add(new InstrucaoVM(Opcode.CMP_GT, null)); break;
                        case ">=": programaBytecode.add(new InstrucaoVM(Opcode.CMP_GE, null)); break;
                    }
                }

                // Salva o resultado armazenado na pilha dentro da variável de destino
                programaBytecode.add(new InstrucaoVM(Opcode.STORE, destino));
            }
        }

        // Finaliza o programa da VM
        programaBytecode.add(new InstrucaoVM(Opcode.HALT, null));
        return programaBytecode;
    }

    private void empilharOperando(String token, List<InstrucaoVM> programa) {
        if (token.equals("true")) {
            programa.add(new InstrucaoVM(Opcode.PUSH, "1"));
        } else if (token.equals("false")) {
            programa.add(new InstrucaoVM(Opcode.PUSH, "0"));
        } else if (token.matches("-?\\d+(\\.\\d+)?")) {
            // Se for um número puro (literal constante), coloca na pilha via PUSH
            programa.add(new InstrucaoVM(Opcode.PUSH, token));
        } else {
            // Se for identificador/variável/temporário, busca o valor usando LOAD
            programa.add(new InstrucaoVM(Opcode.LOAD, token));
        }
    }
}
